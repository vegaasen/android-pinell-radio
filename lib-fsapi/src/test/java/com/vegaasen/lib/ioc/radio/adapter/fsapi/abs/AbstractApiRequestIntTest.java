package com.vegaasen.lib.ioc.radio.adapter.fsapi.abs;

import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiConnection;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import org.junit.BeforeClass;

public abstract class AbstractApiRequestIntTest {

    protected static Host host;

    @BeforeClass
    public static void initialize() {
        host = ApiConnection.INSTANCE.getConnection().getHost().iterator().next();
    }

}
