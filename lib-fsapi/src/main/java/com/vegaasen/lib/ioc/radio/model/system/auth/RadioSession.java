package com.vegaasen.lib.ioc.radio.model.system.auth;

import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;

/**
 * Represents the session of the radio.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
@Immutable
public class RadioSession {

    private final String id;
    private final long when;

    private RadioSession(String id, long when) {
        this.id = id;
        this.when = when;
    }

    public static RadioSession create(final String id) {
        return new RadioSession(id, System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public long getWhen() {
        return when;
    }

    @Override
    public String toString() {
        return String.format("%s*%s", id, when);
    }
}
