package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.fsapi.abs.AbstractApiRequestIntTest;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApiRequestSystemIntTest extends AbstractApiRequestIntTest {

    @Test
    public void obtainNewDeviceSession_normalProcedure() {
        final String result = ApiRequestSystem.INSTANCE.obtainNewDeviceSession(host);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void getCurrentPowerState_normalProcedure() {
        final PowerState result = ApiRequestSystem.INSTANCE.getCurrentPowerState(host);
        assertNotNull(result);
        assertNotEquals(PowerState.UNKNOWN, result);
    }

    @Test
    public void setPowerStateForDevice_turnOn() {
        assertTrue(ApiRequestSystem.INSTANCE.setPowerStateForDevice(host, PowerState.ON));
    }

    @Test
    @Ignore("Dont turn it off :-P")
    public void setPowerStateForDevice_turnOff() throws InterruptedException {
        Thread.sleep(4000);
        assertTrue(ApiRequestSystem.INSTANCE.setPowerStateForDevice(host, PowerState.OFF));
    }

    @Test
    public void getDeviceInformation_normalProcedure() {
        DeviceInformation result = ApiRequestSystem.INSTANCE.getDeviceInformation(host);
        assertNotNull(result);
        assertTrue(result.getName().toLowerCase().contains("pinell"));
    }

    @Test
    public void getCurrentlyPlaying_normalProcedure() {
        DeviceCurrentlyPlaying result = ApiRequestSystem.INSTANCE.getCurrentlyPlaying(host);
        assertNotNull(result);
        System.out.println(result.toString());
    }

    @Test
    public void getDeviceAudioInformation_normalProcedure() {
        DeviceAudio result = ApiRequestSystem.INSTANCE.getDeviceAudioInformation(host);
        assertNotNull(result);
    }

    @Test
    public void getRadioModes_normalProcedure() {
        Set<RadioMode> results = ApiRequestSystem.INSTANCE.getRadioModes(host);
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    public void getCurrentEqualizer_normalProcedure() {
        Equalizer equalizer = ApiRequestSystem.INSTANCE.getEqualizer(host);
        assertNotNull(equalizer);
        assertTrue(equalizer.getName().isEmpty());
        assertFalse(equalizer.getKeyAsString().isEmpty());
    }

}