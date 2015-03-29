package com.vegaasen.lib.ioc.radio.service;

import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

/**
 * Simple interface for communicating with fsapi-enable devices (such as Pinell)
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public interface RadioFsApiService {

    /**
     * Update the provided host with more information regarding the device
     *
     * @param host _
     * @see com.vegaasen.lib.ioc.radio.model.device.DeviceInformation
     */
    void updateHostDeviceInformation(Host host);

    /**
     * Gets the current state
     *
     * @return _
     */
    PowerState getDeviceState();

    /**
     * This simply just sets the device in the wanted state
     *
     * @param state _
     * @return _
     */
    boolean setPowerState(PowerState state);

}
