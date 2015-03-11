package com.vegaasen.http.rest.model.http;

import com.vegaasen.http.rest.model.Scheme;

import java.io.InputStream;
import java.util.Map;

/**
 * Simple implementation of the response that is being passed back from the request (post, get, put or whatever there is)
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class Response {

    private long when;
    private String payload;
    private InputStream originalPayload;
    private Map<String, String> headers;
    private int responseCode;
    private Scheme originalRequestScheme;

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public InputStream getOriginalPayload() {
        return originalPayload;
    }

    public void setOriginalPayload(InputStream originalPayload) {
        this.originalPayload = originalPayload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Scheme getOriginalRequestScheme() {
        return originalRequestScheme;
    }

    public void setOriginalRequestScheme(Scheme originalRequestScheme) {
        this.originalRequestScheme = originalRequestScheme;
    }
}
