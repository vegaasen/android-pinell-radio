package com.vegaasen.http.rest.utils;

import com.vegaasen.http.rest.model.auth.User;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class UsernamePasswordAuthenticator extends Authenticator {

    private final User user;

    public UsernamePasswordAuthenticator(final User user) {
        this.user = user;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be nilled when using BasicAuthentication");
        }
        return (new PasswordAuthentication(
                user.getUserId().getId(),
                user.getPassword().getId().toCharArray())
        );
    }

}
