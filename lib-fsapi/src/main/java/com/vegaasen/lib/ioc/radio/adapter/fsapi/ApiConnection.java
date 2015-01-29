package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponseParts;
import com.vegaasen.lib.ioc.radio.adapter.constants.Parameter;
import com.vegaasen.lib.ioc.radio.adapter.constants.UriContext;
import com.vegaasen.lib.ioc.radio.model.FsApiResponseState;
import com.vegaasen.lib.ioc.radio.model.auth.RadioSession;
import com.vegaasen.lib.ioc.radio.model.conn.Connection;
import com.vegaasen.lib.ioc.radio.model.conn.Host;
import com.vegaasen.lib.ioc.radio.util.TelnetUtil;
import com.vegaasen.lib.ioc.radio.util.XmlUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
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

    private static final int DEFAULT_PORT = 2244, PORT_TRY_2 = 80, DEFAULT_RESPONSE = 403;
    private static final String DEFAULT_CODE = "1234";

    private HttpClient httpClient = HttpClientBuilder.create().build();
    private Connection connection;

    public Connection getConnection() {
        if (connection == null) {
            connection = Connection.create(fetchPotentialHosts());
            massageConnections();
        }
        return connection;
    }

    public void retrieveNewSession(Host host) {
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Parameter.QueryParameter.PIN_NUMBER, host.getCode());
        final Document document = request(getConnection(host, UriContext.Authentication.CREATE_SESSION, parameters));
        if (document != null && verifyResponseOk(document)) {
            host.setRadioSession(RadioSession.create(XmlUtils.INSTANCE.getTextContentByNode(document, ApiResponseParts.SESSION_SESSION_ID)));
        }
    }

    public Document request(URI uri) {
        try {
            return XmlUtils.INSTANCE.getDocument(httpClient.execute(new HttpGet(uri)).getEntity().getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verifyResponseOk(final Document document) {
        return FsApiResponseState.fromStatus(document.getElementsByTagName(ApiResponseParts.RESPONSE_STATUS).item(0).getTextContent()).equals(FsApiResponseState.OK);
    }

    public URI getConnection(final Host host, final String context, final Map<String, String> parameters) {
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

    private String getConnectionRoot(final Host host) {
        return String.format("%s%s", host.getConnectionString(), UriContext.ROOT);
    }

    private void massageConnections() {
        for (final Host host : connection.getHost()) {
            if (!verifyCandidate(host.getConnectionString() + UriContext.ROOT)) {
                connection.getHost().remove(host);
            }
        }
    }

    private Set<Host> fetchPotentialHosts() {
        final Set<Host> hosts = new HashSet<Host>();
        for (final String host : TelnetUtil.findPotentialLocalHosts()) {
            if (TelnetUtil.isAlive(host, DEFAULT_PORT)) {
                hosts.add(Host.create(host, DEFAULT_PORT, DEFAULT_CODE));
            } else if (TelnetUtil.isAlive(host, PORT_TRY_2)) {
                hosts.add(Host.create(host, DEFAULT_PORT, DEFAULT_CODE));
            }
        }
        return hosts;
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

}
