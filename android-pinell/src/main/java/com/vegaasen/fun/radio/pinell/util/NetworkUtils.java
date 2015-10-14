package com.vegaasen.fun.radio.pinell.util;

/**
 * Simple utilities which helps on the work with NetworkUtilities
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public final class NetworkUtils {

    private static final String EMPTY = "";

    private NetworkUtils() {
    }

    public static String fromIntToIp(final int candidate) {
        if (candidate <= 0) {
            return EMPTY;
        }
        return ((candidate & 0xFF) + "." + ((candidate >> 8) & 0xFF) +
                "." + ((candidate >> 16) & 0xFF) + "." + ((candidate >> 24) & 0xFF));
    }

}
