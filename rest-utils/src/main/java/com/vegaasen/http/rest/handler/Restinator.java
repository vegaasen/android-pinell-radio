package com.vegaasen.http.rest.handler;

import com.vegaasen.http.rest.handler.abs.AbstractRestinator;
import com.vegaasen.http.rest.model.auth.Authentication;
import com.vegaasen.http.rest.model.http.ContentType;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.UrlParam;

/**
 * This is the main object where all magic will happen.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class Restinator extends AbstractRestinator {

    private static int connectionTimeout = 20000, socketTimeout = connectionTimeout;

    public Restinator(final String url) {
        scheme.setTo(url);
    }

    public Restinator() {
    }

    public Restinator url(final String url) {
        scheme.setTo(url);
        return this;
    }

    public Restinator authentication(final Authentication authentication) {
        scheme.setAuthentication(authentication);
        return this;
    }

    public Restinator params(final Param... params) {
        if (params != null && params.length > 0) {
            for (final Param param : params) {
                scheme.addParam(param);
            }
        }
        return this;
    }

    public Restinator headers(final Header... headers) {
        if (headers != null && headers.length > 0) {
            for (final Header header : headers) {
                scheme.addHeader(header);
            }
        }
        return this;
    }

    public Restinator urlParam(final UrlParam... params) {
        if (params != null && params.length > 0) {
            for (final UrlParam param : params) {
                scheme.addUrlParam(param);
            }
        }
        return this;
    }

    @Override
    public Restinator addContentType(final ContentType contentType) {
        addContentType(Header.HEADER_CONTENT_TYPE, contentType);
        return this;
    }

    @Override
    public Restinator addAccepts(final ContentType contentType) {
        addContentType(Header.HEADER_ACCEPTS, contentType);
        return this;
    }

    /**
     * Simple method that just returns the current state to string. This will always treat everything as get().
     * POST-data will not be handled here.
     *
     * @return url from scheme
     */
    public String asPlainUrl() {
        return scheme.compileAsString();
    }

    public static int getConnectionTimeout() {
        return connectionTimeout;
    }

    public static int getSocketTimeout() {
        return socketTimeout;
    }

    public static void setTimeout(int connectionTimeout, int socketTimeout) {
        if (connectionTimeout <= 0 || socketTimeout <= 0) {
            throw new IllegalArgumentException("No.");
        }
        Restinator.connectionTimeout = connectionTimeout;
        Restinator.socketTimeout = socketTimeout;
    }

}
