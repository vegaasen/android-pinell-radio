package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

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

}
