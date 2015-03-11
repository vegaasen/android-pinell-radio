package com.vegaasen.http.rest.handler.abs;

import com.vegaasen.http.rest.handler.DefaultHeaders;
import com.vegaasen.http.rest.handler.HttpMethod;
import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.http.ContentType;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.RequestType;
import com.vegaasen.http.rest.model.http.Response;
import com.vegaasen.http.rest.utils.HttpUtils;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public abstract class AbstractRestinator extends AbstractBaseRestinator implements HttpMethod, DefaultHeaders {

    protected final Scheme scheme = new Scheme();

    @Override
    public Response get() {
        return HttpUtils.performGetLikeRequest(RequestType.GET, scheme);
    }

    @Override
    public Response post() {
        return HttpUtils.performGetLikeRequest(RequestType.POST, scheme);
    }

    @Override
    public Response put() {
        return HttpUtils.performGetLikeRequest(RequestType.PUT, scheme);
    }

    @Override
    public Response options() {
        return HttpUtils.performGetLikeRequest(RequestType.OPTIONS, scheme);
    }

    @Override
    public Response delete() {
        return HttpUtils.performGetLikeRequest(RequestType.DELETE, scheme);
    }

    @Override
    public Response head() {
        return HttpUtils.performGetLikeRequest(RequestType.HEAD, scheme);
    }

    protected void addContentType(final String variant, final ContentType contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("ContentType cannot be nilled");
        }
        scheme.addHeader(new Header(variant, contentType.getVariant()));
    }

}
