package com.vegaasen.lib.ioc.radio.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void findPotentialLocalSubnetNetworkHosts_localHosts() {
        final long start = System.currentTimeMillis();
        assertNotNull(TelnetUtil.findPotentialLocalSubnetNetworkHosts());
        final long stop = System.currentTimeMillis();
        System.out.println("Test took ~" + (stop - start) + "ms");
    }

    @Test
    public void findPotentialLocalSubnetNetworkHosts_specificPort_localHosts() {
        final long start = System.currentTimeMillis();
        assertNotNull(TelnetUtil.findPotentialLocalSubnetNetworkHosts("192.168.0", 80));
        final long stop = System.currentTimeMillis();
        System.out.println("Test took ~" + (stop - start) + "ms");
    }

}