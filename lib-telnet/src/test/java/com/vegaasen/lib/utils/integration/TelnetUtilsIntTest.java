package com.vegaasen.lib.utils.integration;

import com.vegaasen.lib.utils.TelnetUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TelnetUtilsIntTest {

    @Test
    public void isAlive_range_ok_normalized() {
        assertFalse(TelnetUtil.isAlive("vg.no", 70, 80).isEmpty());
    }

    @Test
    public void isAlive_ok() {
        assertTrue(TelnetUtil.isAlive("vg.no", 80));
    }

    @Test
    public void isAlive_locally_ok() {
        final boolean alive = TelnetUtil.isAlive("192.168.0.102", 2244);
        assertTrue(alive);
    }

    @Test
    public void findPotentialLocalSubnetNetworkHosts_port2244_localHosts() {
        final long start = System.currentTimeMillis();
        final Set<String> potentialLocalSubnetNetworkHosts = TelnetUtil.findPotentialLocalSubnetNetworkHosts(2244);
        assertNotNull(potentialLocalSubnetNetworkHosts);
        assertEquals(1, potentialLocalSubnetNetworkHosts.size());
        final long stop = System.currentTimeMillis();
        System.out.println("Test took ~" + (stop - start) + "ms" + ". Found " + potentialLocalSubnetNetworkHosts.size());
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

    @Test
    public void isAlive_range_ok() {
        List<Integer> candidates = new ArrayList<>();
        candidates.add(2244);
        candidates.add(80);
        candidates.add(81);
        final Set<Integer> result = TelnetUtil.isAlive("192.168.0.101", candidates);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

}
