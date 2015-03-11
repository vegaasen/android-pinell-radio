package com.vegaasen.http.rest.model.http;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public enum RequestType {

    GET("GET"), POST("POST"), DELETE("DELETE"), PUT("PUT"), HEAD("HEAD"), OPTIONS("OPTIONS");

    private final String type;

    private RequestType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
