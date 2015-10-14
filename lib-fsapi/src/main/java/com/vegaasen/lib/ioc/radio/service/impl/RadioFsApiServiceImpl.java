package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiRequestRadio;
import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiRequestSystem;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
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
    public RadioMode getRadioMode(Host host) {
        if (host == null) {
            LOG.warning("Unable to get radioMode for host. Host is nilled");
            return null;
        }
        return ApiRequestSystem.INSTANCE.getRadioMode(host);
    }

    @Override
    public void setRadioMode(Host host, RadioMode radioMode) {
        if (host == null || radioMode == null) {
            LOG.warning("Unable to set radioMode for host. Host or radioMode is nilled");
            return;
        }
        ApiRequestSystem.INSTANCE.setRadioMode(host, radioMode);
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
        ApiRequestSystem.INSTANCE.setDeviceAudioLevel(host, level);
    }

    @Override
    public Set<Equalizer> listEqualizers(Host host) {
        if (host == null) {
            LOG.warning("Unable to list equalizers for a nilled host");
            return Collections.emptySet();
        }
        final Set<Equalizer> equalizers = ApiRequestSystem.INSTANCE.getEqualizers(host);
        LOG.fine(String.format("Found {%s} equalizers", equalizers.size()));
        return equalizers;
    }

    @Override
    public Equalizer getEqualizer(Host host) {
        if (host == null) {
            LOG.warning("Unable to get equalizer due to either host is nilled");
            return null;
        }
        return ApiRequestSystem.INSTANCE.getEqualizer(host);
    }

    @Override
    public void setEqualizer(Host host, Equalizer equalizer) {
        if (host == null || equalizer == null) {
            LOG.warning("Unable to set requested equalizer due to either host or equalizer is nilled");
            return;
        }
        ApiRequestSystem.INSTANCE.setEqualizer(host, equalizer);
    }

    @Override
    public Set<RadioStation> listStations(Host host, int from, int maxStations) {
        if (host == null) {
            LOG.warning("Unable to listStations due to either host is nilled");
            return null;
        }
        LOG.fine(String.format("Listing stations for {%s} from {%s} and max stations {%s}", host.toString(), from, maxStations));
        return ApiRequestRadio.INSTANCE.getRadioStations(host, from, maxStations);
    }

    @Override
    public Set<RadioStation> enterContainerAndListStations(Host host, RadioStation radioStation, int maxStations) {
        if (host == null || radioStation == null) {
            LOG.warning("Unable to enter container and list stations due to either host or selected folder is nilled/empty");
            return Collections.emptySet();
        }
        LOG.fine(String.format("Selecting container {%s} for {%s}. Showing {%s} elements", radioStation.toString(), host.toString(), maxStations));
        return ApiRequestRadio.INSTANCE.selectContainerAndGetRadioStations(host, radioStation, maxStations);
    }

    @Override
    public Set<RadioStation> enterPreviousContainerAndListStations(Host host, int maxStations) {
        if (host == null) {
            LOG.warning("Unable to move to previous container and list stations due to host is nilled/empty");
            return Collections.emptySet();
        }
        LOG.fine(String.format("Moving to previous container for {%s}. Showing {%s} elements", host.toString(), maxStations));
        return ApiRequestRadio.INSTANCE.selectPreviousContainer(host, maxStations);
    }

    @Override
    public void selectStation(Host host, RadioStation radioStation) {
        if (host == null || radioStation == null) {
            LOG.warning("Unable to selectStation due to either host or selected folder is nilled/empty");
            return;
        }
        LOG.fine(String.format("Selecting station {%s} for {%s}", radioStation.toString(), host.toString()));
        ApiRequestRadio.INSTANCE.selectRadioStation(host, radioStation);
    }

    @Override
    public String fmForwardSearch(Host host) {
        if (host == null) {
            LOG.warning("Unable to fmForwardSearch due to host nilled");
            return null;
        }
        LOG.fine(String.format("FM ForwardSearch for {%s}", host.toString()));
        ApiRequestRadio.INSTANCE.searchFMForward(host);
        return null;
    }

    @Override
    public String fmRewindSearch(Host host) {
        if (host == null) {
            LOG.warning("Unable to fmForwardSearch due to host nilled");
            return null;
        }
        LOG.fine(String.format("FM RewindSearch for {%s}", host.toString()));
        ApiRequestRadio.INSTANCE.searchFMRewind(host);
        return null;
    }

    @Override
    public String getCurrentFMBand(Host host) {
        if (host == null) {
            LOG.warning("Unable to getCurrentFMBand due to host nilled");
            return null;
        }
        LOG.fine(String.format("FM getCurrentBand for {%s}", host.toString()));
        return ApiRequestRadio.INSTANCE.getCurrentFMBand(host);
    }
}
