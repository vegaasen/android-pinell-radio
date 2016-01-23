package com.vegaasen.fun.radio.pinell.async.equalizer;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;

/**
 * In order to simplify the change of active equalizers, use this class implementation
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 16.11.2015
 */
public class EqualizerSelectModeAsync extends AbstractFragmentVoidAsync {

    private final Equalizer selectedEqualizer;

    public EqualizerSelectModeAsync(PinellService pinellService, Equalizer selectedEqualizer) {
        super(pinellService);
        this.selectedEqualizer = selectedEqualizer;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (selectedEqualizer != null) {
            pinellService.setEqualizer(selectedEqualizer);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    @Override
    protected void configureViewComponents() {

    }
}
