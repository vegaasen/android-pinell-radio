package com.vegaasen.http.rest.utils;

import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.RequestType;
import com.vegaasen.http.rest.model.http.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class HttpUtilsTest {

    private Scheme validScheme;

    @Before
    public void setUp() {
        validScheme = new Scheme();
        validScheme.setTo("http://www.vegaasen.com/blog/");
        validScheme.addHeader(new Header("User-Agent", "some-cool-browser"));
    }

    @After
    public void tearDown() {
        validScheme = null;
    }

    @Test
    public void performGetLikeRequest_requestWithGet_ok() {
        validScheme.addParam(new Param("feed", "rss2"));
        final Response response = HttpUtils.performGetLikeRequest(RequestType.GET, validScheme);
        assertNotNull(response);
        assertNotNull(response.getOriginalRequestScheme());
        assertNotNull(response.getHeaders());
        assertNotNull(response.getResponseCode());
        assertNotNull(response.getWhen());
        assertTrue(response.getPayload().contains("rss"));
    }

    @Test
    public void performPostLikeRequest_requestWithPost_ok() {
        validScheme.addParam(new Param("feed", "atom"));
        final Response response = HttpUtils.performPostLikeRequest(RequestType.POST, validScheme);
        assertNotNull(response);
        assertNotNull(response.getOriginalRequestScheme());
        assertNotNull(response.getHeaders());
        assertNotNull(response.getResponseCode());
        assertNotNull(response.getWhen());
        assertTrue(response.getPayload().contains("atom"));
    }

}
