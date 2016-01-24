package com.vegaasen.http.rest.utils.apache;

import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.Response;
import com.vegaasen.http.rest.model.http.UrlParam;
import com.vegaasen.http.rest.utils.translator.ResponseTranslator;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Utilities that utilize Apache Commons / URL instead of Java-stuff.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 24.01.2016
 */
public class HttpUtils {

    private static final int MAX_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
    private static final Logger LOG = Logger.getLogger(HttpUtils.class.getSimpleName());

    public static Response httpGet(final Scheme scheme) {
        if (scheme == null || scheme.getTo() == null) {
            return null;
        }
        LOG.fine(String.format("Performing httpGet for {%s}", scheme));
        HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(MAX_TIMEOUT).build()).build();
        try {
            HttpGet request = new HttpGet(assembleRequestUri(scheme));
            assembleHeaders(scheme, request);
            return ResponseTranslator.translate(httpClient.execute(request));
        } catch (Exception e) {
            LOG.warning(String.format("Unable to perform get on {%s}", scheme));
        }
        return null;
    }

    private static URI assembleRequestUri(final Scheme scheme) {
        if (scheme == null || scheme.getTo() == null) {
            return null;
        }
        try {
            URIBuilder candidate = new URIBuilder(scheme.getTo().toURI());
            if (scheme.getUrlParams() != null) {
                for (UrlParam param : scheme.getUrlParams()) {
                    LOG.fine(String.format("Adding urlParam {%s}", param));
                    candidate.addParameter(param.getKey().getId(), param.getValue().getId());
                }
            }
            return candidate.build();
        } catch (Exception e) {
            LOG.warning("Unable to assemble URI");
        }
        return null;
    }

    private static void assembleHeaders(Scheme scheme, HttpRequestBase request) {
        if (scheme == null || scheme.getHeaders() == null) {
            return;
        }
        for (Header header : scheme.getHeaders()) {
            LOG.fine(String.format("Adding header {%s}", header));
            request.addHeader(header.getKey().getId(), header.getValue().getId());
        }
    }


}
