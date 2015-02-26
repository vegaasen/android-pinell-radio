package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.adapter.constants.Parameter;
import com.vegaasen.lib.ioc.radio.adapter.constants.UriContext;
import com.vegaasen.lib.ioc.radio.model.response.FsApiResponseState;
import com.vegaasen.lib.ioc.radio.model.system.auth.RadioSession;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.util.TelnetUtil;
import com.vegaasen.lib.ioc.radio.util.XmlUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ApiConnection {

    INSTANCE;

    private static final BasicHeader USER_AGENT = new BasicHeader("User-Agent", "Pinell/1.1 CFNetwork/711.1.16 Darwin/14.0.0");
    private static final BasicHeader ACCEPT = new BasicHeader("Accept", "*/*");
    private static final BasicHeader ACCEPT_LANGUAGE = new BasicHeader("Accept-Language", "en-us");
    private static final BasicHeader CONNECTION = new BasicHeader("Connection", "keep-alive");
    private static final BasicHeader ACCEPT_ENCODING = new BasicHeader("Accept-Encoding", "gzip, deflate");
    private static final int DEFAULT_PORT = 2244, PORT_TRY_2 = 80, DEFAULT_RESPONSE = 403;
    private static final String DEFAULT_CODE = "1234";

    private HttpClient httpClient = HttpClientBuilder.create().build();
    private Connection connection;

    /**
     * Before actually connecting to anything, a connection is required to exist.
     *
     * @return your current connection
     */
    public Connection initialize() {
        if (connection == null) {
            connection = Connection.create(fetchPotentialHosts());
            massageConnections();
        }
        return connection;
    }

    public void retrieveNewSession(Host host) {
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Parameter.QueryParameter.PIN_NUMBER, host.getCode());
        final Document document = request(getApiUri(host, UriContext.Authentication.CREATE_SESSION, parameters));
        if (document != null && verifyResponseOk(document)) {
            host.setRadioSession(RadioSession.create(XmlUtils.INSTANCE.getTextContentByNode(document.getDocumentElement(), ApiResponse.SESSION_SESSION_ID)));
        }
    }

    public Document request(URI uri) {
        try {
            final HttpResponse response = conditionallyGetResponse(uri);
            if (response != null) {
                return XmlUtils.INSTANCE.getDocument(response.getEntity().getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse conditionallyGetResponse(URI uri) throws IOException {
        final HttpResponse response = httpClient.execute(assembleGetRequest(uri));
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            return null;
        }
        return response;
    }

    public boolean verifyResponseOk(final Document document) {
        return FsApiResponseState.fromStatus(document.getElementsByTagName(ApiResponse.RESPONSE_STATUS).item(0).getTextContent()).equals(FsApiResponseState.OK);
    }

    public URI getApiUriWithoutRoot(final Host host, final String context) {
        try {
            URIBuilder builder = new URIBuilder(host.getConnectionString() + context);
            for (final Map.Entry<String, String> entry : getDefaultApiConnectionParams(host).entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
            return builder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public URI getApiUri(final Host host, final String context) {
        return getApiUri(host, context, getDefaultApiConnectionParams(host));
    }

    public URI getApiUri(final Host host, final String context, final Map<String, String> parameters) {
        try {
            URIBuilder builder = new URIBuilder(getConnectionRoot(host) + context);
            for (final Map.Entry<String, String> entry : parameters.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
            return builder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getDefaultApiConnectionParams(Host host) {
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Parameter.QueryParameter.PIN_NUMBER, host.getCode());
        parameters.put(Parameter.QueryParameter.SESSION_ID, getSession(host));
        return parameters;
    }

    public Connection reinitialize() {
        connection = null;
        return initialize();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private String getConnectionRoot(final Host host) {
        return String.format("%s%s", host.getConnectionString(), UriContext.ROOT);
    }

    private Set<Host> fetchPotentialHosts() {
        final Set<Host> hosts = new HashSet<Host>();
        for (final String host : TelnetUtil.findPotentialLocalSubnetNetworkHosts()) {
            if (TelnetUtil.isAlive(host, DEFAULT_PORT)) {
                hosts.add(Host.create(host, DEFAULT_PORT, DEFAULT_CODE));
            } else {
                hosts.add(Host.create(host, PORT_TRY_2, DEFAULT_CODE));
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
        final HttpGet get = new HttpGet(URI.create(uri));
        get.setConfig(RequestConfig.DEFAULT);
        try {
            HttpResponse response = httpClient.execute(get);
            return response.getStatusLine().getStatusCode() == DEFAULT_RESPONSE;
        } catch (final IOException e) {
            //*gulp*
        }
        return false;
    }

    private HttpGet assembleGetRequest(final URI uri) {
        final HttpGet get = new HttpGet(uri);
        get.addHeader(USER_AGENT);
        get.addHeader(ACCEPT);
        get.addHeader(ACCEPT_ENCODING);
        get.addHeader(ACCEPT_LANGUAGE);
        get.addHeader(CONNECTION);
        return get;
    }

    private String getSession(Host host) {
        verifyConnection(host);
        return host.getRadioSession().getId();
    }

    private void verifyConnection(final Host host) {
        if (host.getRadioSession() == null) {
            ApiConnection.INSTANCE.retrieveNewSession(host);
        }
    }

}
