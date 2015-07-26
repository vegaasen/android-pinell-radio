package com.vegaasen.http.rest.handler;

import com.vegaasen.http.rest.model.http.Response;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public interface HttpMethodWithUrl {

    Response get(final String url);

    Response post(final String url);

    Response put(final String url);

    Response options(final String url);

    Response delete(final String url);

    Response head(final String url);

}
