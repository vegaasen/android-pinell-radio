package com.vegaasen.http.rest.utils;

import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.UrlParam;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class UrlBuilderTest {

    private String url = "http://www.vegaasen.com";

    @Test(expected = IllegalArgumentException.class)
    public void buildFromScheme_nilledScheme_fail() {
        UrlBuilder.buildFromScheme(null);
    }

    @Test
    public void buildFromScheme_buildWithDifferentParameters_ok() {
        final String key = "key";
        final Scheme scheme = new Scheme();
        scheme.setTo(url);
        scheme.addParam(new Param(key, "value"));
        scheme.addParam(new Param("key2", "value2"));

        final String result = UrlBuilder.buildFromScheme(scheme);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(key));
    }

    @Test
    public void buildFromScheme_buildWithDifferentParametersWithMultipleValues_ok() {
        final String extra1 = "extra1", value3 = "value3", key = "key";
        final Scheme scheme = new Scheme();
        scheme.setTo(url);
        scheme.addParam(new Param(key, "value", extra1, "extra2"));
        scheme.addParam(new Param("key2", "value2", value3, "value4"));

        final String result = UrlBuilder.buildFromScheme(scheme);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(key));
        assertTrue(result.contains(value3));
        assertTrue(result.contains(extra1));
    }

    @Test
    public void buildFromScheme_buildWithNoParameters_ok() {
        final Scheme scheme = new Scheme();
        scheme.setTo(url);

        final String result = UrlBuilder.buildFromScheme(scheme);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(url, result);
    }

    @Test
    public void buildFromScheme_buildWithUrlParameters_ok() {
        final String replaceAbleUrl = "http://www.wtf.omg/{replace}/{me}/{too}/";
        final String expectedUrl = "http://www.wtf.omg/withThisStuff/andWithMe/dontForgetMe/";
        final Scheme scheme = new Scheme();
        scheme.setTo(replaceAbleUrl);
        scheme.addUrlParam(new UrlParam("too", "dontForgetMe"));
        scheme.addUrlParam(new UrlParam("me", "andWithMe"));
        scheme.addUrlParam(new UrlParam("replace", "withThisStuff"));

        final String result = UrlBuilder.buildFromScheme(scheme);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("withThisStuff"));
        assertEquals(expectedUrl, result);
    }

}
