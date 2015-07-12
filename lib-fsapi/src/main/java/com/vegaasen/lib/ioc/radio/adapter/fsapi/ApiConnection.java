package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.http.rest.handler.Restinator;
import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.Response;
import com.vegaasen.http.rest.model.http.ResponseCode;
import com.vegaasen.http.rest.utils.UrlBuilder;
import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.adapter.constants.Parameter;
import com.vegaasen.lib.ioc.radio.adapter.constants.UriContext;
import com.vegaasen.lib.ioc.radio.model.response.FsApiResponseState;
import com.vegaasen.lib.ioc.radio.model.system.auth.RadioSession;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.util.TelnetUtil;
import com.vegaasen.lib.ioc.radio.util.XmlUtils;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Todo: remove all e.printStackTrace()..
 * Todo: better test-coverage
 */
public enum ApiConnection {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(ApiConnection.class.getSimpleName());
    //todo: change the header so that it identifies the library correctly.
    private static final Header USER_AGENT = new Header("User-Agent", "Pinell/1.1 CFNetwork/711.3.18 Darwin/14.0.0");
    private static final Header ACCEPT = new Header("Accept", "*/*");
    private static final Header ACCEPT_LANGUAGE = new Header("Accept-Language", "en-us");
    private static final Header CONNECTION = new Header("Connection", "keep-alive");
    private static final Header ACCEPT_ENCODING = new Header("Accept-Encoding", "gzip, deflate");
    private static final int DEFAULT_FS_PORT = 2244, ALTERNATIVE_FS_PORT = 80;
    private static final String DEFAULT_CODE = "1234";
    private static final String EMPTY = "";

    private Connection connection;
    //In certain cases, it would be nice to let the user specify the subnet
    private String preSubnet;

    /**
     * Before actually connecting to anything, a connection is required to exist.
     *
     * @return your current connection
     */
    public Connection initialize() {
        if (connection == null) {
            connection = Connection.create(fetchPotentialHosts(preSubnet));
            massageConnections();
        }
        return connection;
    }

    public void retrieveNewSession(Host host) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(Parameter.QueryParameter.PIN_NUMBER, host.getCode());
        final Document document = request(getApiUri(host, UriContext.Authentication.CREATE_SESSION, parameters));
        if (document != null && verifyResponseOk(document)) {
            host.setRadioSession(RadioSession.create(XmlUtils.INSTANCE.getTextContentByNode(document.getDocumentElement(), ApiResponse.SESSION_SESSION_ID)));
        }
    }

    public Document request(URI uri) {
        try {
            final Response response = conditionallyGetResponse(uri);
            if (response != null) {
                final String payload = response.getPayload();
                if (payload != null && !payload.isEmpty()) {
                    return XmlUtils.INSTANCE.getDocument(payload);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verifyResponseOk(final Document document) {
        return FsApiResponseState.fromStatus(document.getElementsByTagName(ApiResponse.RESPONSE_STATUS).item(0).getTextContent()).equals(FsApiResponseState.OK);
    }

    public URI getApiUriWithoutRoot(final Host host, final String context) {
        final Scheme urlScheme = new Scheme();
        urlScheme.setTo(host.getConnectionString() + context);
        for (final Map.Entry<String, String> entry : getDefaultApiConnectionParams(host).entrySet()) {
            urlScheme.addParam(new Param(entry.getKey(), entry.getValue()));
        }
        try {
            return UrlBuilder.buildFromSchemeAsUri(urlScheme);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public URI getApiUri(final Host host, final String context) {
        return getApiUri(host, context, getDefaultApiConnectionParams(host));
    }

    public URI getApiUri(final Host host, final String context, final Map<String, String> parameters) {
        final Scheme urlScheme = new Scheme();
        urlScheme.setTo(getConnectionRoot(host) + context);
        for (final Map.Entry<String, String> entry : parameters.entrySet()) {
            urlScheme.addParam(new Param(entry.getKey(), entry.getValue()));
        }
        try {
            return UrlBuilder.buildFromSchemeAsUri(urlScheme);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getDefaultApiConnectionParams(Host host) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(Parameter.QueryParameter.PIN_NUMBER, host.getCode());
        parameters.put(Parameter.QueryParameter.SESSION_ID, getSession(host));
        return parameters;
    }

    /**
     * Remove the current connection
     */
    public void detach() {
        connection = null;
    }

    public Connection reinitialize() {
        detach();
        return initialize();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setPreSubnet(String preSubnet) {
        this.preSubnet = preSubnet;
    }

    private Response conditionallyGetResponse(URI uri) throws IOException {
        final Response response = new Restinator(uri.toString()).headers(USER_AGENT, ACCEPT, ACCEPT_ENCODING, ACCEPT_LANGUAGE, CONNECTION).get();
        if (response != null && response.getResponseCode() == ResponseCode.NOT_FOUND) {
            return null;
        }
        return response;
    }

    private String getConnectionRoot(final Host host) {
        return String.format("%s%s", host.getConnectionString(), UriContext.ROOT);
    }

    /**
     * @param preEnteredSubnet - if this variable exists, it will try to use the entered subnet instead of relying on the TelnetUtils to find the current one
     * @return hosts
     */
    private Set<Host> fetchPotentialHosts(final String preEnteredSubnet) {
        LOG.info(String.format("Fetching potentialHosts for {%s}", preEnteredSubnet));
        final Set<Host> hosts = new HashSet<>();
        for (final String host : preEnteredSubnet == null || preEnteredSubnet.isEmpty() ?
                TelnetUtil.findPotentialLocalSubnetNetworkHosts(DEFAULT_FS_PORT) :
                TelnetUtil.findPotentialLocalSubnetNetworkHosts(preEnteredSubnet, DEFAULT_FS_PORT)) {
            if (TelnetUtil.isAlive(host, DEFAULT_FS_PORT)) {
                LOG.info(String.format("Found potential FSRadio client on {%s}", host));
                hosts.add(Host.create(host, DEFAULT_FS_PORT, DEFAULT_CODE));
            } else {
                if (TelnetUtil.isAlive(host, ALTERNATIVE_FS_PORT)) {
                    hosts.add(Host.create(host, ALTERNATIVE_FS_PORT, DEFAULT_CODE));
                } else {
                    LOG.fine(String.format("Unable to connect to host {%s} on either of the ports {%s, %s}", host, DEFAULT_FS_PORT, ALTERNATIVE_FS_PORT));
                }
            }
        }
        return hosts;
    }

    private void massageConnections() {
        for (final Host host : connection.getHost()) {
            if (!verifyCandidate(host.getConnectionString() + UriContext.ROOT)) {
                connection.getHost().remove(host);
            }
        }
    }

    /**
     * Used in order to verify that the wanted connection is as it should be.
     *
     * @param uri _
     * @return _
     */
    private boolean verifyCandidate(final String uri) {
        try {
            final Response response = new Restinator(uri).get();
            return response.getResponseCode() == ResponseCode.FORBIDDEN;
        } catch (final Exception e) {
            //*gulp*
        }
        return false;
    }

    private String getSession(Host host) {
        verifyConnection(host);
        return host == null || host.getRadioSession() == null ? EMPTY : host.getRadioSession().getId();
    }

    private void verifyConnection(final Host host) {
        if (host.getRadioSession() == null) {
            ApiConnection.INSTANCE.retrieveNewSession(host);
        }
    }

}
