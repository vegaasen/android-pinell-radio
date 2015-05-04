package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.helper.HelperUtils;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class RadioFsApiServiceImplIntTest {

    private RadioFsApiService service;
    private Host host;

    @Before
    public void setUp() {
        host = Host.create(HelperUtils.VEGARDS_PINELL_HOST, 2244, "1234");
        assertNotNull(host);
        service = new RadioFsApiServiceImpl();
    }

    @Test
    public void getDeviceState_normalProcedure() {
        PowerState state = service.getDeviceState(host);
        assertNotNull(state);
        assertNotEquals(PowerState.UNKNOWN, state);
    }

    @Test
    public void setPowerState_on() throws InterruptedException {
        final PowerState expectedState = PowerState.ON;
        boolean state = service.setPowerState(host, expectedState);
        assertNotNull(state);
        assertTrue(state);
        Thread.sleep(1500);
        assertEquals(expectedState, service.getDeviceState(host));
    }

    @Test
    public void setPowerState_off() throws InterruptedException {
        final PowerState expectedState = PowerState.OFF;
        boolean state = service.setPowerState(host, expectedState);
        assertNotNull(state);
        assertTrue(state);
        Thread.sleep(1500);
        assertEquals(expectedState, service.getDeviceState(host));
    }

    @Test
    public void getDeviceInformation_normalProcedure() {
        final DeviceInformation information = service.getDeviceInformation(host);
        assertNotNull(information);
    }

    @Test
    public void getCurrentlyPlaying_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        DeviceCurrentlyPlaying currentlyPlaying = service.getCurrentlyPlaying(host);
        assertNotNull(currentlyPlaying);
        turnDeviceOff();
    }

    @Test
    public void getCurrentAudioInformation_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        DeviceAudio audio = service.getCurrentAudioInformation(host);
        assertNotNull(audio);
        turnDeviceOff();
    }

    @Test
    public void listAvailableRadioModes_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        Set<RadioMode> radioModes = service.listAvailableRadioModes(host);
        assertNotNull(radioModes);
        turnDeviceOff();
    }

    private void turnDeviceOff() {
        service.setPowerState(host, PowerState.OFF);
    }

    private void turnDeviceOn() throws InterruptedException {
        if (service.getDeviceState(host) == PowerState.OFF) {
            service.setPowerState(host, PowerState.ON);
            Thread.sleep(2000);
        }
    }

}