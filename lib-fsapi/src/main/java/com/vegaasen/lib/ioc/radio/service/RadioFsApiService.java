package com.vegaasen.lib.ioc.radio.service;

import com.vegaasen.lib.ioc.radio.model.PowerState;

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
    public PowerState getState();

    /**
     * This simply just sets the device in the wanted state
     *
     * @param state _
     * @return _
     */
    public boolean power(PowerState state);

}
