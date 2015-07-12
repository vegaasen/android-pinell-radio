package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;

import java.util.Set;

/**
 * Services related to radio-tasks. Example:
 * - Currently playing
 * - Browsing
 * - Equalizer
 * - ..
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
interface PinellRadioService {

    /**
     * Get the currently playing element on the radio
     *
     * @return _
     */
    DeviceCurrentlyPlaying getCurrentlyPlaying();

    /**
     * Mutes the radio
     *
     * @return volume muted from
     */
    int setAudioMuted();

    /**
     * Get the current device audio levels, included the current state (muted, unmuted etc)
     *
     * @return _
     */
    DeviceAudio getAudioLevels();

    /**
     * Set the wanted audioLevel for the Pinell-device
     *
     * @param level (0-40)
     */
    void setAudioLevel(int level);

    /**
     * List all available equalizers that is configured in the device
     *
     * @return available equalizers
     */
    Set<Equalizer> listEqualizers();

}
