package com.vegaasen.http.rest.model.auth;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public enum AuthenticationType {

    BASIC("basic"), DIGEST("digest");

    private final String id;

    private AuthenticationType(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
