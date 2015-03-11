package com.vegaasen.http.rest.integration.handler;

import com.vegaasen.http.rest.handler.Restinator;
import com.vegaasen.http.rest.model.http.ContentType;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.Response;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class RestinatorTest {

    private static final String VALID_URL = "http://www.vegaasen.com/";
    private static final String PINELL_LOCAL_HOST = "http://192.168.0.104:2244";

    @Test
    public void get_getDeviceInformation() {
        final Response response = new Restinator(PINELL_LOCAL_HOST + "/device").get();
        assertNotNull(response);
    }

    @Test
    public void post_createNewSession() {
        final Response response = new Restinator(PINELL_LOCAL_HOST + "/fsapi/CREATE_SESSION").params(new Param("pin", "1234")).get();
        assertNotNull(response);
        System.out.println(response.getPayload());
    }

    @Test
    public void asPlainUrl_retrieveAsPlainString_ok() {
        final String expectedUrl = "http://www.vegaasen.com?param1=value1;value2";
        final String plainUrl = new Restinator("http://www.vegaasen.com").params(new Param("param1", "value1", "value2")).asPlainUrl();
        assertNotNull(plainUrl);
        assertFalse(plainUrl.isEmpty());
        assertEquals(expectedUrl, plainUrl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void asPlainUrl_noUrlDefined_fail() {
        new Restinator().asPlainUrl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void asPlainUrl_illegalUrl_fail() {
        new Restinator("ops.com");
    }

    @Test
    public void asPlainUrl_UrnDoesNotMakeSenseButNoFail_ok() {
        final String expected = "http:is:some:urn";
        final String result = new Restinator(expected).asPlainUrl();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expected, result);
    }

    @Test
    public void asPlainUrl_failNoUrlDefinedThenAddUrlAndWork_ok() {
        final String expected = "http:wherever";
        try {
            new Restinator().asPlainUrl();
            fail();
        } catch (final IllegalArgumentException e) {
            assertTrue(true);
        }
        final String result = new Restinator().url(expected).asPlainUrl();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expected, result);
    }

    @Test
    public void addAccepts_addAccepts_ok() {
        final Response r = new Restinator(VALID_URL).addAccepts(ContentType.WHATEVER).get();
        assertNotNull(r);
    }

}
