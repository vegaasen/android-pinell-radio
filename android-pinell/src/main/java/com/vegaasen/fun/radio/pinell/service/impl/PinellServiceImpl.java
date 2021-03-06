package com.vegaasen.fun.radio.pinell.service.impl;

import android.util.Log;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiConnection;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiConnectionService;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;
import com.vegaasen.lib.ioc.radio.service.impl.RadioFsApiConnectionServiceImpl;
import com.vegaasen.lib.ioc.radio.service.impl.RadioFsApiServiceImpl;

import java.util.Collections;
import java.util.Set;

/**
 * Implementation of the pinellService as the default main interface for all android application specific tasks
 * <p/>
 * TODO: split this into several abstract classes or several classes in general?
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 21.03.2015
 */
public class PinellServiceImpl implements PinellService {

    public static final int AUDIO_LEVEL_MUTE = 0, AUDIO_LEVEL_UNKNOWN = -1;

    private static final String TAG = PinellServiceImpl.class.getSimpleName();

    private RadioFsApiConnectionService radioFsApiConnectionService;
    private RadioFsApiService radioFsApiService;
    private Connection connection;
    private String currentSubnet;
    private Host selectedHost;

    public PinellServiceImpl() {
        this.radioFsApiConnectionService = new RadioFsApiConnectionServiceImpl();
        this.radioFsApiService = new RadioFsApiServiceImpl();
    }

    @Override
    public boolean isPoweredOn() {
        return getRadioService().getDeviceState(getSelectedHost()) == PowerState.ON;
    }

    @Override
    public void setPowerState(PowerState powerState) {
        if (powerState == null) {
            Log.w(TAG, "Unable to handle a nilled powerState");
            return;
        }
        Log.w(TAG, String.format("Attempting to alter the powerState to {%s}", powerState));
        radioFsApiService.setPowerState(getSelectedHost(), powerState);
    }

    @Override
    public boolean isHostConfigured() {
        return getSelectedHost() != null;
    }

    @Override
    public Host assembleHost(HostBean candidate) {
        if (candidate == null || Strings.isNullOrEmpty(candidate.getIpAddress())) {
            Log.w(TAG, "Unable to create host by the selected candidate as the candidate is nilled, no ipAddress detected or no ports opened");
            return null;
        }
        return getRadioConnectionService().createHost(
                candidate.getIpAddress(),
                assembleCandidatePort(candidate.getPortsOpen()));
    }

    @Override
    public boolean setCurrentPinellHost(int index) {
        Log.d(TAG, String.format("Setting pinellHost from index {%s}", index));
        if (index >= getPinellHosts().size()) {
            Log.w(TAG, "The wanted index was greater than the hostsList. Ignoring");
            return false;
        }
        setSelectedHost(Lists.newArrayList(getPinellHosts()).get(index));
        return true;
    }

    @Override
    public boolean setCurrentPinellHost(Host host) {
        if (host == null) {
            return false;
        }
        setSelectedHost(host);
        return true;
    }

    @Override
    public Set<Host> getPinellHosts() {
        final Connection currentConnection = getPinellConnection();
        if (currentConnection == null) {
            Log.w(TAG, "The current connection seems to be nilled. Unable to find hosts");
            return Collections.emptySet();
        }
        return currentConnection.getHost();
    }

    @Override
    public Connection getPinellConnection() {
        return getPinellConnection(false);
    }

    @Override
    public Connection getPinellConnection(final boolean refresh) {
        Log.d(TAG, String.format("Trying to fetch pinellConnection. Refresh? {%s}", refresh));
        if (getConnection() == null || refresh) {
            setConnection(
                    refresh ?
                            getRadioConnectionService().redetectPotentialHosts() :
                            getRadioConnectionService().getPotentialHosts()
            );
        }
        return getConnection();
    }

    @Override
    public Host getSelectedHost() {
        return getSelectedHost(false);
    }

    @Override
    public Host getSelectedHost(boolean update) {
        if (selectedHost != null && update) {
            updateCurrentHost();
        }
        return selectedHost;
    }

    @Override
    public void setCurrentSubnet(String currentSubnet) {
        this.currentSubnet = currentSubnet;
        getRadioConnectionService().setSubnet(currentSubnet);
    }

    @Override
    public void updateHostBeanDetails(HostBean candidate) {
        Host host = getRadioConnectionService().createHost(
                candidate.getIpAddress(),
                assembleCandidatePort(candidate.getPortsOpen()));
        if (host == null) {
            return;
        }
        getRadioService().updateHostDeviceInformation(host);
        if (host.getDeviceInformation() == null) {
            return;
        }
        candidate.setHostname(host.getDeviceInformation().getName());
    }

    @Override
    public DeviceAudio getAudioLevels() {
        return getRadioService().getCurrentAudioInformation(getSelectedHost());
    }

    @Override
    public boolean isPinellDevice() {
        return isPinellDevice(getSelectedHost());
    }

    @Override
    public boolean isPinellDevice(Host host) {
        return host != null && getRadioConnectionService().isValidDevice(host);
    }

    @Override
    public DeviceCurrentlyPlaying getCurrentlyPlaying() {
        return getRadioService().getCurrentlyPlaying(getSelectedHost());
    }

    @Override
    public int setAudioMuted() {
        DeviceAudio audio = getAudioLevels();
        setAudioLevel(AUDIO_LEVEL_MUTE);
        return audio != null ? audio.getLevel() : AUDIO_LEVEL_UNKNOWN;
    }

    @Override
    public synchronized void setAudioLevel(int level) {
        if (level < 0) {
            Log.i(TAG, String.format("Audio level {%s} wanted, resetting to default setAudioMuted-level {%s} instead", level, AUDIO_LEVEL_MUTE));
            level = AUDIO_LEVEL_MUTE;
        }
        getRadioService().setAudioLevel(getSelectedHost(), level);
    }

    @Override
    public Set<Equalizer> listEqualizers() {
        return getRadioService().listEqualizers(getSelectedHost());
    }

    @Override
    public void setEqualizer(Equalizer equalizer) {
        if (equalizer == null) {
            Log.w(TAG, "Expected equalizer, but no item provided");
            return;
        }
        getRadioService().setEqualizer(getSelectedHost(), equalizer);
    }

    @Override
    public Equalizer getCurrentEqualizer() {
        return getRadioService().getEqualizer(getSelectedHost());
    }

    @Override
    public Set<RadioMode> listInputSources() {
        return getRadioService().listAvailableRadioModes(getSelectedHost());
    }

    @Override
    public void setInputSource(RadioMode radioMode) {
        if (radioMode == null || !radioMode.isSelectable()) {
            Log.w(TAG, "Unable to set the radioMode as the radioMode seems to be nilled or not selectable");
            return;
        }
        getRadioService().setRadioMode(getSelectedHost(), radioMode);
    }

    @Override
    public RadioMode getCurrentInputSource() {
        return getRadioService().getRadioMode(getSelectedHost());
    }

    @Override
    public Set<RadioStation> listRadioStations(int from) {
        if (from < -1) {
            from = RadioFsApiService.DEFAULT_START_INDEX;
        }
        Set<RadioStation> radioStations = getRadioService().listStations(getSelectedHost(), from, RadioFsApiService.DEFAULT_MAX_ITEMS);
        if (CollectionUtils.isEmpty(radioStations)) {
            radioStations = Collections.emptySet();
        }
        Log.d(TAG, String.format("Found {%s} radioStations", radioStations.size()));
        return radioStations;
    }

    @Override
    public Set<RadioStation> enterContainerAndListStations(RadioStation radioContainer) {
        if (radioContainer == null) {
            Log.w(TAG, "Ops, it seems like the radioContainer is nilled");
            return Collections.emptySet();
        }
        if (radioContainer.isRadioStation()) {
            Log.w(TAG, String.format("It seems like the selected container {%s} is actually a station. How did you manage this?", radioContainer.toString()));
            return Collections.emptySet();
        }
        return getRadioService().enterContainerAndListStations(getSelectedHost(), radioContainer, RadioFsApiService.DEFAULT_MAX_ITEMS);
    }

    @Override
    public void setRadioStation(RadioStation radioStation) {
        if (radioStation == null) {
            Log.d(TAG, "Unable to select the radio station as it seems to be nilled");
            return;
        }
        getRadioService().selectStation(getSelectedHost(), radioStation);
    }

    @Override
    public void searchFMBandForward() {
        getRadioService().fmForwardSearch(getSelectedHost());
    }

    @Override
    public void searchFMBandRewind() {
        getRadioService().fmRewindSearch(getSelectedHost());
    }

    private RadioFsApiConnectionService getRadioConnectionService() {
        return radioFsApiConnectionService;
    }

    private RadioFsApiService getRadioService() {
        return radioFsApiService;
    }

    private Connection getConnection() {
        return connection;
    }

    private void setSelectedHost(Host selectedHost) {
        Log.i(TAG, String.format("Setting {%s} as the selected host", selectedHost.getHost()));
        this.selectedHost = selectedHost;
        updateCurrentHost();
    }

    private void setConnection(final Connection connection) {
        Log.i(TAG, String.format("Connection {%s} has been set", connection.toString()));
        this.connection = connection;
    }

    private void updateCurrentHost() {
        try {
            getRadioService().updateHostDeviceInformation(getSelectedHost());
        } catch (Exception e) {
            Log.e(TAG, String.format("Unable to update the current host {%s} with device information", getSelectedHost().getHost()));
        }
    }

    /**
     * Detects the default candidate port. This should be rewritten. Not proud if this.
     *
     * @param candidates _
     * @return _
     */
    private static int assembleCandidatePort(Set<Integer> candidates) {
        if (CollectionUtils.isEmpty(candidates)) {
            Log.d(TAG, "No ports were detected using the default detector. Considering alternatives");
            return ApiConnection.DEFAULT_FS_PORT;
        }
        Log.i(TAG, String.format("Assembling candidate port by {%s}", candidates.toArray()));
        if (candidates.contains(ApiConnection.DEFAULT_FS_PORT)) {
            Log.d(TAG, "Default port detected, returning this");
            return ApiConnection.DEFAULT_FS_PORT;
        }
        return ApiConnection.ALTERNATIVE_FS_PORT;
    }
}
