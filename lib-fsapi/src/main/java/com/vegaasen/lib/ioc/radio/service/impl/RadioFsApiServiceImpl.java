package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiRequestSystem;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class RadioFsApiServiceImpl implements RadioFsApiService {

    private static final Logger LOG = Logger.getLogger(RadioFsApiServiceImpl.class.getSimpleName());

    @Override
    public PowerState getDeviceState(Host host) {
        if (host == null) {
            LOG.warning("Nilled host provided. Returning UNKNOWN state");
            return PowerState.UNKNOWN;
        }
        LOG.info(String.format("Fetching the device powerState for host {%s}", host.toString()));
        return ApiRequestSystem.INSTANCE.getCurrentPowerState(host);
    }

    @Override
    public boolean setPowerState(Host host, PowerState state) {
        if (host == null || state == null) {
            LOG.warning("Unable to set the powerState to host, as one of the values seems to be nilled.");
            return false;
        }
        if (state == PowerState.UNKNOWN) {
            LOG.info("Unable to set the powerState to UNKNOWN. Skipping. How did you manage that? :-)");
            return false;
        }
        LOG.info(String.format("Setting the device powerState {%s} for host {%s}", state.name(), host.toString()));
        return ApiRequestSystem.INSTANCE.setPowerStateForDevice(host, state);
    }

    @Override
    public void updateHostDeviceInformation(Host host) {
        if (host == null) {
            LOG.warning("The host seems to be nilled. Unable to proceed");
            return;
        }
        DeviceInformation deviceInformation = ApiRequestSystem.INSTANCE.getDeviceInformation(host);
        if (deviceInformation == null) {
            LOG.warning(String.format("Host provided was not nilled, however no device information was detected regarding the host {%s}", host.getHost()));
            return;
        }
        host.setDeviceInformation(deviceInformation);
    }

    @Override
    public DeviceInformation getDeviceInformation(Host host) {
        updateHostDeviceInformation(host);
        if (host.getDeviceInformation() == null) {
            LOG.warning(String.format("Unable to get device information for host {%s}", host.toString()));
            return null;
        }
        return host.getDeviceInformation();
    }

    @Override
    public Set<RadioMode> listAvailableRadioModes(Host host) {
        if (host == null) {
            LOG.warning("Unable to get information for host. Host is nilled");
            return Collections.emptySet();
        }
        return ApiRequestSystem.INSTANCE.getRadioModes(host);
    }

    @Override
    public DeviceCurrentlyPlaying getCurrentlyPlaying(Host host) {
        if (host == null) {
            LOG.warning("Unable to get information for host. Host is nilled");
            return null;
        }
        return ApiRequestSystem.INSTANCE.getCurrentlyPlaying(host);
    }

    @Override
    public DeviceAudio getCurrentAudioInformation(Host host) {
        if (host == null) {
            LOG.warning("Unable to get information for host. Host is nilled");
            return null;
        }
        return ApiRequestSystem.INSTANCE.getDeviceAudioInformation(host);
    }

    @Override
    public void setAudioLevel(Host host, int level) {
        if (host == null || (level > 100 || level < 0)) {
            LOG.warning(String.format("Unable to set audioLevel to the wanted level due to invalid level {%s}", level));
            return;
        }
        //todo: implement the set-routine
    }

    @Override
    public Set<Equalizer> listEqualizers(Host host) {
        return null;
    }

    @Override
    public void setEqualizer(Host host, Equalizer equalizer) {

    }
}
