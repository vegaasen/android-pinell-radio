package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

/**
 * Used in regards to the verification process of Pinell devices.
 * Example on verification:
 * - Is the selected device actually a Pinell device?
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 24.5.2015
 */
interface PinellVerificationService {

    /**
     * Is the current selected host a Pinell device, or something else?
     *
     * @return _
     */
    boolean isPinellDevice();

    /**
     * Is the selected host actually a Pinell device, or something else?
     *
     * @param host _
     * @return verification
     */
    boolean isPinellDevice(Host host);

}
