package com.vegaasen.fun.radio.pinell.model;

import com.google.common.base.Strings;

/**
 * Represents the various RadioModes that is possible to perform browsing to.
 * Currently, these are the following radioModes that is allowed and will be handled:
 * <p/>
 * - DAB
 * - Internet Radio
 * - FM / AM
 * <p/>
 * The "name" attribute should be the same as whatever being returned from the listings of input sources.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 30.08.2015
 * @see com.vegaasen.lib.ioc.radio.model.system.RadioMode
 * @since 30.08.2015
 */
public enum PinellRadioMode {

    DAB("DAB"), INTERNET_RADIO("Internet radio"), FM_AM("FM"), MUSIC_PLAYER("Music player"), AUX("Auxiliary Input"), UNKNOWN("");

    private final String name;

    PinellRadioMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PinellRadioMode fromName(final String candidate) {
        if (Strings.isNullOrEmpty(candidate)) {
            return UNKNOWN;
        }
        for (PinellRadioMode type : values()) {
            if (candidate.equals(type.getName())) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
