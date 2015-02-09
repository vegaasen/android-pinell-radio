package com.vegaasen.lib.ioc.radio.model;

import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PowerStateTest {

    @Test
    public void fromState_notFound() {
        assertEquals(PowerState.UNKNOWN, PowerState.fromState(-123));
    }

    @Test
    public void fromState_on() {
        assertEquals(PowerState.ON, PowerState.fromState(1));
    }

    @Test
    public void fromState_off() {
        assertEquals(PowerState.OFF, PowerState.fromState(0));
    }

}