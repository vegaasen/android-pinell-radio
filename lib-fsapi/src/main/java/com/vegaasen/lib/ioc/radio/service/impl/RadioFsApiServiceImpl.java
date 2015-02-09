package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class RadioFsApiServiceImpl implements RadioFsApiService {

    @Override
    public PowerState getDeviceState() {
        return null;
    }

    @Override
    public boolean setPowerState(PowerState state) {
        return false;
    }
}
