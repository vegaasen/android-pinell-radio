package com.vegaasen.http.rest.model;

import com.vegaasen.http.rest.model.abs.AbstractScheme;
import com.vegaasen.http.rest.utils.UrlBuilder;

import java.io.Serializable;

/**
 * Simple scheme. This is the holder for all configuration that is to be sent to the Connector.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class Scheme extends AbstractScheme implements Serializable {

    @Override
    public String compileAsString() {
        return UrlBuilder.buildFromScheme(this);
    }

}
