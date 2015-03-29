package com.vegaasen.fun.radio.pinell.service.impl;

import android.util.Log;
import com.google.common.collect.Lists;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiConnectionService;
import com.vegaasen.lib.ioc.radio.service.impl.RadioFsApiConnectionServiceImpl;

import java.util.Collections;
import java.util.Set;

/**
 * Implementation of the pinellService
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 21.03.2015
 */
public class PinellServiceImpl implements PinellService {

    private static final String TAG = PinellServiceImpl.class.getSimpleName();

    private RadioFsApiConnectionService radioFsApiConnectionService;
    private Connection connection;
    private String currentSubnet;
    private Host selectedHost;

    public PinellServiceImpl() {
        this.radioFsApiConnectionService = new RadioFsApiConnectionServiceImpl();
    }

    @Override
    public boolean setCurrentPinellHost(int index) {
        Log.d(TAG, String.format("Setting pinellHost from index {%s}", index));
        if (index >= getPinellHosts().size()) {
            Log.w(TAG, "The wanted index was greater than the hostsList. Ignoring action.");
            return false;
        }
        setSelectedHost(Lists.newArrayList(getPinellHosts()).get(index));
        return true;
    }

    @Override
    public Set<Host> getPinellHosts() {
        final Connection currentConnection = getPinellConnection();
        if (currentConnection == null) {
            Log.w(TAG, "The current connection seems to be nilled. Unable to find hosts");
            return Collections.emptySet();
        }
        return currentConnection.getHost();
    }

    @Override
    public Connection getPinellConnection() {
        return getPinellConnection(false);
    }

    @Override
    public Connection getPinellConnection(final boolean refresh) {
        Log.d(TAG, String.format("Trying to fetch pinellConnection. Refresh? {%s}", refresh));
        if (getConnection() == null || refresh) {
            setConnection(
                    refresh ?
                            getService().redetectPotentialHosts() :
                            getService().getPotentialHosts()
            );
        }
        return getConnection();
    }

    public void setCurrentSubnet(String currentSubnet) {
        this.currentSubnet = currentSubnet;
        getService().setSubnet(currentSubnet);
    }

    public Host getSelectedHost() {
        return selectedHost;
    }

    private void setSelectedHost(Host selectedHost) {
        Log.i(TAG, String.format("Setting {%s} as the selected host", selectedHost.getHost()));
        this.selectedHost = selectedHost;
    }

    private RadioFsApiConnectionService getService() {
        return radioFsApiConnectionService;
    }

    private Connection getConnection() {
        return connection;
    }

    private void setConnection(final Connection connection) {
        Log.i(TAG, String.format("Connection {%s} has been set", connection.toString()));
        this.connection = connection;
    }
}
