package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.model.PowerState;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiService;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class RadioFsApiServiceImpl implements RadioFsApiService {

    @Override
    public PowerState getState() {
        return null;
    }

    @Override
    public boolean power(PowerState state) {
        return false;
    }
}
