package com.vegaasen.http.rest.model.auth;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class Authentication {

    private final AuthenticationType authenticationType;
    private final User user;

    public Authentication(final AuthenticationType type, final User user) {
        this.authenticationType = type;
        this.user = user;
    }

    public Authentication(final User user) {
        this.authenticationType = AuthenticationType.BASIC;
        this.user = user;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public User getUser() {
        return user;
    }
}
