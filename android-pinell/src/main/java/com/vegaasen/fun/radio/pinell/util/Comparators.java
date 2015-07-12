package com.vegaasen.fun.radio.pinell.util;

import com.vegaasen.lib.ioc.radio.model.system.Equalizer;

import java.util.Comparator;

/**
 * Collection of comparators out there
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public final class Comparators {

    private Comparators() {
    }

    public static final class EqualizerComparator implements Comparator<Equalizer> {

        @Override
        public int compare(Equalizer one, Equalizer two) {
            return one.getName().compareTo(two.getName());
        }
    }

}
