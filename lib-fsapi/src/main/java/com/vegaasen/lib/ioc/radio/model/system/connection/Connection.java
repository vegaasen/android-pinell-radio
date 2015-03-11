package com.vegaasen.lib.ioc.radio.model.system.connection;

import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a connection and its details. Immutable
 *
 * @author vegaasen
 */
@Immutable
public class Connection {

    private final Set<Host> host;
    private final long when;

    private Connection(Set<Host> host, long when) {
        this.host = host;
        this.when = when;
    }

    public static Connection create(final Host host) {
        final Set<Host> hosts = new HashSet<Host>();
        hosts.add(host);
        return create(hosts);
    }

    public static Connection create(final Set<Host> host) {
        return new Connection(host, System.currentTimeMillis());
    }

    public Set<Host> getHost() {
        return new HashSet<Host>(host);
    }

    public long getWhen() {
        return when;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "host=" + host +
                ", when=" + when +
                '}';
    }
}
