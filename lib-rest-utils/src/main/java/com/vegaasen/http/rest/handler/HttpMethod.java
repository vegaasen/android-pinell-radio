package com.vegaasen.http.rest.handler;

import com.vegaasen.http.rest.model.http.Response;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public abstract interface HttpMethod {

    Response get();

    Response post();

    Response put();

    Response options();

    Response delete();

    Response head();

}
