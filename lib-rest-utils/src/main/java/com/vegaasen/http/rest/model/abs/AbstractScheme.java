package com.vegaasen.http.rest.model.abs;

import com.vegaasen.http.rest.model.auth.Authentication;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.UrlParam;
import com.vegaasen.http.rest.utils.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * todo: replace addHeader with something like "conditionallyAddHeader" - or replace the header if already added..
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public abstract class AbstractScheme {

    private static final String HTTPS = "https";

    protected URL to;
    protected Authentication authentication;
    protected List<Header> headers = new ArrayList<>();
    protected List<Param> params = new ArrayList<>();
    protected List<UrlParam> urlParams = new ArrayList<>();

    public abstract String compileAsString();

    public boolean isHttpsActivated() {
        if (to == null) {
            throw new IllegalArgumentException("Unable to use https on nilled to address");
        }
        return !StringUtils.isBlank(to.getProtocol()) && to.getProtocol().equalsIgnoreCase(HTTPS);
    }

    public URL getTo() {
        return to;
    }

    public void setTo(String to) {
        try {
            this.to = new URL(to);
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<Param> getParams() {
        return params;
    }

    public List<UrlParam> getUrlParams() {
        return urlParams;
    }

    public void addHeader(final Header header) {
        if (header == null) {
            return;
        }
        headers.add(header);
    }

    public void addParam(final Param param) {
        if (param == null) {
            return;
        }
        params.add(param);
    }

    public void addUrlParam(final UrlParam urlParam) {
        if (urlParam == null) {
            return;
        }
        urlParams.add(urlParam);
    }

}
