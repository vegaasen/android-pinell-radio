package com.vegaasen.lib.ioc.radio.model.system.auth;

/**
 * Represents the session of the radio.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
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
}
