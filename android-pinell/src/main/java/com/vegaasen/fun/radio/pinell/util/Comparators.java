package com.vegaasen.fun.radio.pinell.util;

import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

import java.util.Comparator;

/**
 * Collection of comparators out there
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 26.7.2015
 * @since 25.5.2015
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

    public static final class InputSourceComparator implements Comparator<RadioMode> {

        @Override
        public int compare(RadioMode one, RadioMode two) {
            return one.getName().compareTo(two.getName());
        }
    }

    public static final class RadioStationsComparator implements Comparator<RadioStation> {

        @Override
        public int compare(RadioStation one, RadioStation two) {
            return one.getName().compareTo(two.getName());
        }
    }

}
