package com.vegaasen.http.rest.handler.abs;

import com.vegaasen.http.rest.handler.HttpMethodWithUrl;
import com.vegaasen.http.rest.model.http.Response;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
abstract class AbstractBaseRestinator implements HttpMethodWithUrl {

    @Override
    public Response get(String url) {
        return null;
    }

    @Override
    public Response post(String url) {
        return null;
    }

    @Override
    public Response put(String url) {
        return null;
    }

    @Override
    public Response options(String url) {
        return null;
    }

    @Override
    public Response delete(String url) {
        return null;
    }

    @Override
    public Response head(String url) {
        return null;
    }
}
