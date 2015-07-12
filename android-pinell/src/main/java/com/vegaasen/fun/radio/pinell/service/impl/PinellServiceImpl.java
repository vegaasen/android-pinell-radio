package com.vegaasen.fun.radio.pinell.service.impl;

import android.util.Log;
import com.google.common.collect.Lists;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiConnectionService;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;
import com.vegaasen.lib.ioc.radio.service.impl.RadioFsApiConnectionServiceImpl;
import com.vegaasen.lib.ioc.radio.service.impl.RadioFsApiServiceImpl;

import java.util.Collections;
import java.util.Set;

/**
 * Implementation of the pinellService
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
    public boolean setCurrentPinellHost(int index) {
        Log.d(TAG, String.format("Setting pinellHost from index {%s}", index));
        if (index >= getPinellHosts().size()) {
            Log.w(TAG, "The wanted index was greater than the hostsList. Ignoring action.");
            return false;
        }
        setSelectedHost(Lists.newArrayList(getPinellHosts()).get(index));
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
}
