package com.vegaasen.http.rest.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class StringUtilsTest {

    @Test
    public void isBlank_nullString_false() {
        assertTrue(StringUtils.isBlank((String) null));
    }

    @Test
    public void isBlank_emptyString_false() {
        assertTrue(StringUtils.isBlank(""));
    }

    @Test
    public void isBlank_whitespacedString_false() {
        assertTrue(StringUtils.isBlank(" "));
    }

    @Test
    public void isBlank_validString_true() {
        assertFalse(StringUtils.isBlank("Heya"));
    }

    @Test
    public void isNull_nullString_false() {
        assertTrue(StringUtils.isNull(null));
    }

    @Test
    public void isNull_emptyString_false() {
        assertFalse(StringUtils.isNull(""));
    }

}
