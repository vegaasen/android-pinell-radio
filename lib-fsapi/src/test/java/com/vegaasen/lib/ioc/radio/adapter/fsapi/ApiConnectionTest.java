package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.model.conn.Connection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApiConnectionTest {

    @Test
    public void assemble_connection() {
        final Connection connection = ApiConnection.INSTANCE.getConnection();
        assertNotNull(connection);
        final Connection connection2 = ApiConnection.INSTANCE.getConnection();
        assertEquals(connection, connection2);
    }

}