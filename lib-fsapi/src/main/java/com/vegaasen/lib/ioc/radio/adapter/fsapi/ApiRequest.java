package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponseParts;
import com.vegaasen.lib.ioc.radio.adapter.constants.Parameter;
import com.vegaasen.lib.ioc.radio.adapter.constants.UriContext;
import com.vegaasen.lib.ioc.radio.model.PowerState;
import com.vegaasen.lib.ioc.radio.model.conn.Host;
import com.vegaasen.lib.ioc.radio.util.XmlUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;


/**
 * Simple class which basically just connects to the API. All of these methods requires a host is present
 * Todo: errohandling, errorhandling, errorhandling.
 *
 * @author vegaasen
 */
public enum ApiRequest {

    INSTANCE;

    private HttpClient httpClient = HttpClientBuilder.create().build();

    /**
     * Gets a new session for the provided hots
     *
     * @param host where
     * @return sessionId
     */
    public String getNewSession(Host host) {
        ApiConnection.INSTANCE.retrieveNewSession(host);
        return host.getRadioSession().getId();
    }

    /**
     * Get the current state of the power
     *
     * @param host where
     * @return _
     */
    public PowerState getCurrentPowerState(Host host) {
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getConnection(host, UriContext.System.POWER_STATE_CURRENT, getDefaultApiParams(host)));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            return PowerState.fromState(XmlUtils.INSTANCE.getNumberContentByNode(document, ApiResponseParts.VALUE_U_8));
        }
        return PowerState.UNKNOWN;
    }

    /**
     * Set the state of the power to the device (on / off)
     *
     * @param host  where
     * @param state what
     * @return status
     */
    public boolean setPowerStateForDevice(Host host, PowerState state) {
        final Map<String, String> params = getDefaultApiParams(host);
        params.put(Parameter.QueryParameter.VALUE, state.getStateAsString());
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getConnection(host, UriContext.System.POWER_STATE_SET, params));
        return document != null && ApiConnection.INSTANCE.verifyResponseOk(document);
    }

    private Map<String, String> getDefaultApiParams(Host host) {
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(Parameter.QueryParameter.PIN_NUMBER, host.getCode());
        parameters.put(Parameter.QueryParameter.SESSION_ID, getSession(host));
        return parameters;
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

    private void removeSession(Host host) {
        host.removeRadioSession();
    }

}
