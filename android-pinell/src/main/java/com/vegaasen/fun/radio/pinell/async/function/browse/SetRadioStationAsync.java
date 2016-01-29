package com.vegaasen.fun.radio.pinell.async.function.browse;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

/**
 * Sets a new RadioStation as the current active
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @version 22.11.2015
 * @since 22.11.2015
 */
public class SetRadioStationAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = SetRadioStationAsync.class.getSimpleName();

    private final RadioStation candidate;

    public SetRadioStationAsync(PinellService pinellService, RadioStation radioStation) {
        super(pinellService);
        this.candidate = radioStation;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, String.format("RadioStation {%s} selected. Switching to this station", candidate.toString()));
        pinellService.setRadioStation(candidate);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void configureViewComponents() {

    }
}