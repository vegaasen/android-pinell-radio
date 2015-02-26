package com.vegaasen.lib.ioc.radio.service.impl;

import com.vegaasen.lib.ioc.radio.model.system.connection.Connection;
import com.vegaasen.lib.ioc.radio.service.RadioFsApiConnectionService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RadioFsApiConnectionServiceImplTest {
    
    private RadioFsApiConnectionService service;
    
    @Before
    public void setUp() {
        service = new RadioFsApiConnectionServiceImpl();
    }
    
    @Test
    public void getPotentialHosts_normalProcedure() {
        final Connection result = service.getPotentialHosts();
        assertNotNull(result);
        assertNotNull(result.getHost());
    }

}