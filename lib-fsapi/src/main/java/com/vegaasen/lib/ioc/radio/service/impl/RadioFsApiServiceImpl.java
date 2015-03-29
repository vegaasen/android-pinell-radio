package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiSystemRequest;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class RadioFsApiServiceImpl implements RadioFsApiService {

    private static final Logger LOG = Logger.getLogger(RadioFsApiServiceImpl.class.getSimpleName());

    @Override
    public void updateHostDeviceInformation(Host host) {
        if (host == null) {
            LOG.warning("The host seems to be nilled. Unable to proceed");
            return;
        }
        DeviceInformation deviceInformation = ApiSystemRequest.INSTANCE.getDeviceInformation(host);
        if (deviceInformation == null) {
            LOG.warning(String.format("Host provided was not nilled, however no device information was detected regarding the host {%s}", host.getHost()));
            return;
        }
        host.setDeviceInformation(deviceInformation);
    }

    @Override
    public PowerState getDeviceState() {
        return null;
    }

    @Override
    public boolean setPowerState(PowerState state) {
        return false;
    }
}
