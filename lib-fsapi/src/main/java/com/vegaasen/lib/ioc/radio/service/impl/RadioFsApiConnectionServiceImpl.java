package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.http.rest.utils.StringUtils;
import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiConnection;
import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiRequestSystem;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiConnectionService;

public class RadioFsApiConnectionServiceImpl implements RadioFsApiConnectionService {

    @Override
    public boolean isValidDevice(Host host) {
        try {
            return !StringUtils.isBlank(ApiRequestSystem.INSTANCE.obtainNewDeviceSession(host));
        } catch (Exception e) {
            //*gulp*
        }
        return false;
    }

    @Override
    public Connection getPotentialHosts() {
        return ApiConnection.INSTANCE.initialize();
    }

    @Override
    public Connection redetectPotentialHosts() {
        return ApiConnection.INSTANCE.reinitialize();
    }

    @Override
    public Connection configureConnection(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Required parameter was nilled");
        }
        ApiConnection.INSTANCE.setConnection(connection);
        return getPotentialHosts();
    }

    @Override
    public void setSubnet(final String subnet) {
        if (subnet == null || subnet.isEmpty()) {
            throw new IllegalArgumentException("Required parameter was nilled or empty");
        }
        ApiConnection.INSTANCE.setPreSubnet(subnet);
        ApiConnection.INSTANCE.detach();
    }

    @Override
    public Host createHost(String host, int port) {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Required parameter was nilled or empty");
        }
        return ApiConnection.INSTANCE.createHost(host, port);
    }
}
