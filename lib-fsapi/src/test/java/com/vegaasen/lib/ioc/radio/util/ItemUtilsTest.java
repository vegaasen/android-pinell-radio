package com.vegaasen.lib.ioc.radio.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ItemUtilsTest {

    private static final String WHATEVER = "whatever";

    @Test
    public void normalizeRadioStationName_nilled() {
        assertNull(ItemUtils.normalizeRadioStationName(null));
    }

    @Test
    public void normalizeRadioStationName_empty() {
        final String object = ItemUtils.normalizeRadioStationName("");
        assertNotNull(object);
        assertTrue(object.isEmpty());
    }

    @Test
    public void normalizeRadioStationName_normal() {
        assertEquals(WHATEVER, ItemUtils.normalizeRadioStationName(WHATEVER));
    }

    @Test
    public void normalizeRadioStationName_normal_withQuestion() {
        assertEquals(WHATEVER, ItemUtils.normalizeRadioStationName("?" + WHATEVER));
    }

    @Test
    public void normalizeRadioStationName_normal_withQuestionAndTab() {
        assertEquals(WHATEVER, ItemUtils.normalizeRadioStationName("?" + WHATEVER + "   "));
    }

    @Test
    public void normalizeRadioStationName_normal_withQuestionAndTabAndSpace() {
        assertEquals(WHATEVER, ItemUtils.normalizeRadioStationName("?" + WHATEVER + "            "));
    }

    @Test
    public void normalizeRadioStationName_normal_withQuestionAndTabAndSpaces() {
        final String actual = ItemUtils.normalizeRadioStationName("?" + WHATEVER + "            " + WHATEVER);
        assertEquals(WHATEVER + " " + WHATEVER, actual);
    }

}