package com.vegaasen.fun.radio.pinell.service;

import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.Set;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
interface PinellHostService {

    /**
     * Convenience method for checking wether the host is configured or not
     *
     * @return _
     */
    boolean isHostConfigured();

    /**
     * Assemble a host based on the provided hostBean
     *
     * @param hostBean _
     * @return _
     */
    Host assembleHost(HostBean hostBean);

    /**
     * Set the wanted pinellHost by the index. The index is determined by the host of indexes.
     *
     * @param index _
     * @return _
     */
    boolean setCurrentPinellHost(int index);

    /**
     * Set the wanted pinellHost by a supplied host. This will also update the existing details related
     * to the selected host
     *
     * @param host _
     * @return _
     */
    boolean setCurrentPinellHost(Host host);

    /**
     * Get a list of all the availble pinellHosts
     *
     * @return _
     */
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

    /**
     * Fetch the currently selected host
     *
     * @return _
     */
    Host getSelectedHost();

    /**
     * Fetch the currently selected host, and specify wether to update the host at the same time (device information that is)
     *
     * @param update _
     * @return _
     */
    Host getSelectedHost(boolean update);

    /**
     * Set the current subnet. Can be nilled.
     * Use WifiManager in order to fetch the current subnet
     *
     * @param currentSubnet _
     */
    void setCurrentSubnet(String currentSubnet);

    /**
     * Fetches the host-details
     */
    void updateHostBeanDetails(HostBean host);

}
