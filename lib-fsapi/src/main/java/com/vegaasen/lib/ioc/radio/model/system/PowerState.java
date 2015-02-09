package com.vegaasen.lib.ioc.radio.model.system;

/**
 * Represents the state of the device
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public enum PowerState {

    ON(1), OFF(0), UNKNOWN(-1);

    private final int state;

    private PowerState(final int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public String getStateAsString() {
        return Integer.toString(state);
    }

    public static PowerState fromState(int state) {
        for (final PowerState s : values()) {
            if (state == s.getState()) {
                return s;
            }
        }
        return UNKNOWN;
    }

}
