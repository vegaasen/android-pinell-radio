package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.helper.HelperUtils;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    @After
    public void tearDown() {
        turnDeviceOff();
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
    }

    @Test
    public void listAvailableRadioModes_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        Set<RadioMode> radioModes = service.listAvailableRadioModes(host);
        assertNotNull(radioModes);
    }

    @Test
    public void getRadioMode_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        RadioMode radioMode = service.getRadioMode(host);
        assertNotNull(radioMode);
    }

    @Test
    public void setRadioMode_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        RadioMode originalMode = service.getRadioMode(host);
        List<RadioMode> radioModes = new ArrayList<>(service.listAvailableRadioModes(host));
        service.setRadioMode(host, radioModes.get(1));
        Thread.sleep(2000);
        service.setRadioMode(host, originalMode);
    }

    @Test
    public void getCurrentAudioInformation_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        DeviceAudio audio = service.getCurrentAudioInformation(host);
        assertNotNull(audio);
    }

    @Test
    public void setAudioLevel_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        final int expectedLevel = 20;
        service.setAudioLevel(host, expectedLevel);
        DeviceAudio audio = service.getCurrentAudioInformation(host);
        assertNotNull(audio);
        assertEquals(expectedLevel, audio.getLevel());
    }

    @Test
    public void listEqualizers_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        Set<Equalizer> equalizers = service.listEqualizers(host);
        assertNotNull(equalizers);
    }

    @Test
    public void getEqualizer_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        Equalizer equalizer = service.getEqualizer(host);
        assertNotNull(equalizer);
    }

    @Test
    public void setEqualizer_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        Equalizer originallySet = service.getEqualizer(host);
        assertNotNull(originallySet);
        List<Equalizer> equalizers = new ArrayList<>(service.listEqualizers(host));
        assertNotNull(equalizers);
        assertTrue(equalizers.size() > 1);
        final Equalizer expectedEqualizer = equalizers.get(2);
        service.setEqualizer(host, expectedEqualizer);
        Equalizer newSet = service.getEqualizer(host);
        assertNotNull(newSet);
        assertEquals(expectedEqualizer.getKey(), newSet.getKey());
        service.setEqualizer(host, originallySet);
    }

    @Test
    public void listStations_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        Set<RadioStation> radioStations = service.listStations(host, RadioFsApiServiceImpl.DEFAULT_START_INDEX, RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS);
        assertNotNull(radioStations);
        assertFalse(radioStations.isEmpty());
    }

    @Test
    public void enterContainerAndListStations_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        final Set<RadioMode> radioModes = service.listAvailableRadioModes(host);
        RadioMode radioModeSelected = null;
        for(RadioMode radioMode : radioModes) {
            if(radioMode.getKey() == 0) {
                radioModeSelected = radioMode;
            }
        }
        service.setRadioMode(host, radioModeSelected);
        List<RadioStation> radioStations = new ArrayList<>(service.listStations(host, RadioFsApiServiceImpl.DEFAULT_START_INDEX, RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS));
        assertNotNull(radioStations);
        assertFalse(radioStations.isEmpty());
        Set<RadioStation> oneLevelDownStation = service.enterContainerAndListStations(host, radioStations.get(0), RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS);
        assertNotNull(oneLevelDownStation);
        assertFalse(oneLevelDownStation.isEmpty());
    }

    @Test
    public void enterPreviousContainerAndListStations_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        List<RadioStation> radioStations = new ArrayList<>(service.listStations(host, RadioFsApiServiceImpl.DEFAULT_START_INDEX, RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS));
        assertNotNull(radioStations);
        assertFalse(radioStations.isEmpty());
        Set<RadioStation> oneLevelUpStation = service.enterContainerAndListStations(host, radioStations.get(0), RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS);
        assertNotNull(oneLevelUpStation);
        Set<RadioStation> oneLevelDownStation = service.enterPreviousContainerAndListStations(host, RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS);
        assertNotNull(oneLevelDownStation);
        assertFalse(oneLevelDownStation.isEmpty());
        assertNotEquals(oneLevelUpStation.size(), oneLevelDownStation.size());
    }

    @Test
    public void selectStation_normalProcedure() throws InterruptedException {
        turnDeviceOn();
        List<RadioStation> radioStations = new ArrayList<>(service.listStations(host, RadioFsApiServiceImpl.DEFAULT_START_INDEX, RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS));
        assertNotNull(radioStations);
        assertFalse(radioStations.isEmpty());
        if (!radioStations.get(0).isRadioStation()) {
            radioStations = new ArrayList<>(service.enterContainerAndListStations(host, radioStations.get(0), RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS));
            if (!radioStations.isEmpty() && !radioStations.get(0).isRadioStation()) {
                radioStations = new ArrayList<>(service.enterContainerAndListStations(host, radioStations.get(0), RadioFsApiServiceImpl.DEFAULT_MAX_ITEMS));
            }
        }
        service.selectStation(host, radioStations.get(0));
        Thread.sleep(1500);
        DeviceCurrentlyPlaying playing = service.selectStation(host, radioStations.get(1));
        System.out.println(playing.toString());
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