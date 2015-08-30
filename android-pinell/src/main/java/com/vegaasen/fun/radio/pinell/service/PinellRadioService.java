package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

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

    /**
     * Set a new current equalizer for the device
     *
     * @param equalizer _
     */
    void setEqualizer(Equalizer equalizer);

    /**
     * Get the currently defined equalizer
     *
     * @return the currently defined equalizer
     */
    Equalizer getCurrentEqualizer();

    /**
     * List all available inputSources for the Pinell device
     *
     * @return available inputSources / radioModes
     */
    Set<RadioMode> listInputSources();

    /**
     * Set the wanted radioMode / inputSource to the Pinell Device
     *
     * @param radioMode _
     */
    void setInputSource(RadioMode radioMode);

    /**
     * Get the current defined inputSource (AUX, DAB, InternetRadio etc)
     *
     * @return _
     */
    RadioMode getCurrentInputSource();

    /**
     * List all radioStations available in the current selected radio mode (e.g DAB, Internet Radio etc)
     *
     * @param from from which index should the search start?
     * @return potential radio stations
     */
    Set<RadioStation> listRadioStations(int from);

    /**
     * Enter the selected RadioStation (as its a container) and list its content
     *
     * @param radioContainer the radio container
     * @return the detected radioStations
     */
    Set<RadioStation> enterContainerAndListStations(RadioStation radioContainer);

    /**
     * Select the chosen radioStation. This will choose the selected radio station
     *
     * @param radioStation _
     */
    DeviceCurrentlyPlaying setRadioStation(RadioStation radioStation);

    /**
     * Trigger a new search for FM stations forward
     */
    void searchFMBandForward();

    /**
     * Trigger a new search for FM stations in rewind
     */
    void searchFMBandRewind();

}
