package com.vegaasen.http.rest.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class SchemeTest {

    @Test
    public void isHttpsActivated_shouldNotBeActiveOnHttp() {
        final Scheme scheme = new Scheme();
        scheme.setTo("http://www.no");
        assertFalse(scheme.isHttpsActivated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void isHttpsActivated_shouldNotBeActiveNilled() {
        final Scheme scheme = new Scheme();
        scheme.isHttpsActivated();
    }

    @Test
    public void isHttpsActivated_shouldBeActive() {
        final Scheme scheme = new Scheme();
        scheme.setTo("https://www.vg.no");
        assertTrue(scheme.isHttpsActivated());
    }

}
