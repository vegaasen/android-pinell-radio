package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiConnection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiConnectionService;

public class RadioFsApiConnectionServiceImpl implements RadioFsApiConnectionService {

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
}
