package com.vegaasen.lib.ioc.radio.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TelnetUtilTest {

    @Test
    public void isAlive_range_ok() {
        assertFalse(TelnetUtil.isAlive("vg.no", new int[]{70, 80}).isEmpty());
    }

    @Test
    public void isAlive_ok() {
        assertTrue(TelnetUtil.isAlive("vg.no", 80));
    }

    @Test
    public void findPotentialLocalHosts_localHosts() {
        assertNotNull(TelnetUtil.findPotentialLocalHosts());
    }

}