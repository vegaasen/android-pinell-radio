package com.vegaasen.fun.radio.pinell.async.function;

import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

import java.util.Collections;
import java.util.List;

/**
 * Simple async-adapter that fetches all radio stations
 * <p/>
 * Note: Must be called from within a async task.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 23.01.2016
 */
public class GetAllRadioStationsAsync {

    private final PinellService pinellService;

    public GetAllRadioStationsAsync(PinellService pinellService) {
        this.pinellService = pinellService;
    }

    public List<RadioStation> assembleRadioStations() {
        return sortRadioStations(CollectionUtils.toList(pinellService.listRadioStations(0)));
    }

    private List<RadioStation> sortRadioStations(final List<RadioStation> radioStations) {
        Collections.sort(radioStations, new Comparators.RadioStationsComparator());
        return radioStations;
    }

}
