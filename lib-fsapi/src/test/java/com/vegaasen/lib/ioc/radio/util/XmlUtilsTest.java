package com.vegaasen.lib.ioc.radio.util;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XmlUtilsTest {

    @Test
    public void getDocument_nilled_fail() {
        assertNull(XmlUtils.INSTANCE.getDocument((InputStream) null));
    }

    @Test
    public void getTextContentByNode_nilled_ok() {
        assertEquals("", XmlUtils.INSTANCE.getTextContentByNode(null, null));
    }

    @Test
    public void getTextContentByNode_nilled_empty_ok() {
        assertEquals("", XmlUtils.INSTANCE.getTextContentByNode(null, ""));
    }

    @Test
    public void getNumberContentByNode_nilled_empty_ok() {
        assertEquals(0, XmlUtils.INSTANCE.getNumberContentByNode(null, ""));
    }

}