package com.vegaasen.lib.ioc.radio.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 26.11.2015
 */
public final class RandomUtils {

    private RandomUtils() {
    }

    public static int random() {
        return ThreadLocalRandom.current().nextInt(100000 - 1000) + 1;
    }

    public static String randomAsString() {
        return Integer.toString(random());
    }

}
