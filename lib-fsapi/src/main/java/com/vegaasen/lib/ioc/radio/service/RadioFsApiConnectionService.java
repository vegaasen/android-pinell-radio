package com.vegaasen.lib.ioc.radio.service;

import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;

/**
 * This interface simplifies the connecting to the FsAPI itself. Users may also use the class ApiConnection.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @see com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiConnection
 */
public interface RadioFsApiConnectionService {

    /**
     * Finds and returns a set of potential FsAPI-enabled hosts on the net you're currently connected to.
     *
     * @return potential hosts
     */
    Connection getPotentialHosts();

    /**
     * Tries to detect hosts. This is the same as the getPotentialHosts()-however, it will remove the previous found
     * hosts
     *
     * @return potential hosts
     */
    Connection redetectPotentialHosts();

    /**
     * In certain cases, it would be nice to configure the IP and stuff if you already have this at hand.
     * This helps on that subject
     *
     * @param connection _
     * @return _
     */
    Connection configureConnection(Connection connection);

    /**
     * In some cases it is good to set the subnet from an external source. This methods helps in this regard.
     * Requires to be called first.
     *
     * @param subnet _
     */
    void setSubnet(String subnet);

}
