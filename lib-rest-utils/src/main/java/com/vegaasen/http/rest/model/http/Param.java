package com.vegaasen.http.rest.model.http;

import com.vegaasen.http.rest.model.abs.ExtendedKeyValueVariant;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class Param extends ExtendedKeyValueVariant {

    public Param(final String key, final String... value) {
        super(key, value);
    }
}
