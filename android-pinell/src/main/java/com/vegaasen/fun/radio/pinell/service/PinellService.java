package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.Set;

/**
 * ..what..
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public interface PinellService {

    Set<Host> getPinellHosts();

    /**
     * Fetch a new (or existing if already found and connected) Connection to the Pinell device
     *
     * @return _
     */
    Connection getPinellConnection();

    /**
     * Fetch a conditionally new (or existing) connection to the Pinell-device.
     *
     * @param refresh determines if the connection should be reset
     * @return _
     */
    Connection getPinellConnection(boolean refresh);

}
