package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.fsapi.abs.AbstractApiRequestIntTest;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ApiRequestRadioIntTest extends AbstractApiRequestIntTest {

    @Test
    public void getRadioStations_normalProcedure() {
        Set<RadioStation> result = ApiRequestRadio.INSTANCE.getRadioStations(host, 0, 20);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

}