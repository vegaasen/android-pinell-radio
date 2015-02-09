package com.vegaasen.lib.ioc.radio.model.system.connection;

import java.util.Set;

/**
 * Represents a connection and its details
 *
 * @author vegaasen
 */
public class Connection {

    private final Set<Host> host;
    private final long when;

    private Connection(Set<Host> host, long when) {
        this.host = host;
        this.when = when;
    }

    public static Connection create(Set<Host> host) {
        return new Connection(host, System.currentTimeMillis());
    }

    public Set<Host> getHost() {
        return host;
    }

    public long getWhen() {
        return when;
    }

}
