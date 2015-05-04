package com.vegaasen.lib.ioc.radio.service;

import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.Set;

/**
 * Simple interface for communicating with fsapi-enable devices (such as Pinell)
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public interface RadioFsApiService {

    /**
     * Gets the current state
     *
     * @return _
     */
    PowerState getDeviceState(Host host);

    /**
     * This simply just sets the device in the wanted state
     *
     * @param state _
     * @return _
     */
    boolean setPowerState(Host host, PowerState state);

    /**
     * Update the provided host with more information regarding the device
     *
     * @param host _
     * @see com.vegaasen.lib.ioc.radio.model.device.DeviceInformation
     */
    void updateHostDeviceInformation(Host host);

    /**
     * Fetch the device information for the supplied host
     *
     * @param host _
     * @return _
     */
    DeviceInformation getDeviceInformation(Host host);

    /**
     * Get the available radioModes supported by this device
     *
     * @return _
     */
    Set<RadioMode> listAvailableRadioModes(Host host);

    /**
     * Get the currently active playing tune, radio station or similar
     *
     * @return _
     */
    DeviceCurrentlyPlaying getCurrentlyPlaying(Host host);

    /**
     * Get the current audio information from the radio - such as muted, not-muted, audio level etc.
     *
     * @param host _
     * @return _
     */
    DeviceAudio getCurrentAudioInformation(Host host);

    /**
     * Set the specified audioLevel (0-100 [??]) for a specific host
     *
     * @param host  _
     * @param level - may be 0-100
     */
    void setAudioLevel(Host host, int level);

    /**
     * Get the list of available equalizers for the provided host
     *
     * @param host _
     * @return _
     */
    Set<Equalizer> listEqualizers(Host host);

    /**
     * Configure new equalizer for the specified host
     *
     * @param host      _
     * @param equalizer _
     */
    void setEqualizer(Host host, Equalizer equalizer);

}
