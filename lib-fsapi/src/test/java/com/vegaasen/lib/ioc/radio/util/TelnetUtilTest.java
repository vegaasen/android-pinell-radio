package com.vegaasen.lib.ioc.radio.util;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
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
    public void findPotentialLocalSubnetNetworkHosts_port2244_localHosts() {
        final long start = System.currentTimeMillis();
        final Set<String> potentialLocalSubnetNetworkHosts = TelnetUtil.findPotentialLocalSubnetNetworkHosts(2244);
        assertNotNull(potentialLocalSubnetNetworkHosts);
        assertEquals(1, potentialLocalSubnetNetworkHosts.size());
        final long stop = System.currentTimeMillis();
        System.out.println("Test took ~" + (stop - start) + "ms");
    }

    @Test
    public void findPotentialLocalSubnetNetworkHosts_port80_localHosts() {
        final long start = System.currentTimeMillis();
        assertNotNull(TelnetUtil.findPotentialLocalSubnetNetworkHosts(80));
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