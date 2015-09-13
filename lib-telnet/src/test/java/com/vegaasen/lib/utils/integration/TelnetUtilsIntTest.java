package com.vegaasen.lib.utils.integration;

import com.vegaasen.lib.utils.TelnetUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class TelnetUtilsIntTest {

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
