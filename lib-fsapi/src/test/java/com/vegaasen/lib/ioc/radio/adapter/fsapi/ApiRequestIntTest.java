package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.model.PowerState;
import com.vegaasen.lib.ioc.radio.model.conn.Host;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApiRequestIntTest {

    private static Host host;

    @BeforeClass
    public static void initialize() {
        host = ApiConnection.INSTANCE.getConnection().getHost().iterator().next();
    }

    @Test
    public void getNewSession_normalProcedure() {
        final String result = ApiRequest.INSTANCE.getNewSession(host);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void getCurrentPowerState_normalProcedure() {
        final PowerState result = ApiRequest.INSTANCE.getCurrentPowerState(host);
        assertNotNull(result);
        assertNotEquals(PowerState.UNKNOWN, result);
    }

    @Test
    public void setPowerStateForDevice_turnOn() {
        assertTrue(ApiRequest.INSTANCE.setPowerStateForDevice(host, PowerState.ON));
    }

    @Test
//    @Ignore
    public void setPowerStateForDevice_turnOff() throws InterruptedException {
        Thread.sleep(4000);
        assertTrue(ApiRequest.INSTANCE.setPowerStateForDevice(host, PowerState.OFF));
    }

}