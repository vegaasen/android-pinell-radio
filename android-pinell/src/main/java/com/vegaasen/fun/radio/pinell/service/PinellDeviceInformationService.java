package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.lib.ioc.radio.model.system.PowerState;

/**
 * This interface relates itself to the device information related portions.
 * All methods accepts Host as an input
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @see com.vegaasen.lib.ioc.radio.model.system.connection.Host
 * @since 9.4.2015
 */
interface PinellDeviceInformationService {

    /**
     * Is the device turned on?
     *
     * @return state
     */
    boolean isPoweredOn();

    /**
     * Set the powerState of the radio. If the state is alread "ON", it will be ignored. If not, the device will be on and vice versa.
     *
     * @param powerState _
     */
    void setPowerState(PowerState powerState);

}
