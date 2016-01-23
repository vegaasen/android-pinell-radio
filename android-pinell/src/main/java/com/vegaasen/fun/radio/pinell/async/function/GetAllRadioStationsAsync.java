package com.vegaasen.fun.radio.pinell.async.function;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

import java.util.Collections;
import java.util.List;

/**
 * Simple async-adapter that fetches all radio stations
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 23.01.2016
 */
public class GetAllRadioStationsAsync extends AbstractFragmentAsync<List<RadioStation>> {

    private final int from;

    public GetAllRadioStationsAsync(PinellService pinellService, int from) {
        super(pinellService);
        this.from = from;
    }

    public GetAllRadioStationsAsync(PinellService pinellService) {
        super(pinellService);
        from = 0;
    }

    @Override
    protected List<RadioStation> doInBackground(List<RadioStation>... lists) {
        return assembleRadioStations(from);
    }

    @Override
    protected void onPostExecute(List<RadioStation> aVoid) {
    }

    @Override
    protected void configureViewComponents() {
    }

    private List<RadioStation> assembleRadioStations(int from) {
        return sortRadioStations(CollectionUtils.toList(pinellService.listRadioStations(from)));
    }

    private List<RadioStation> sortRadioStations(final List<RadioStation> radioStations) {
        Collections.sort(radioStations, new Comparators.RadioStationsComparator());
        return radioStations;
    }

}
