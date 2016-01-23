package com.vegaasen.fun.radio.pinell.async.function.search;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class FmSearchAsync extends AbstractFragmentVoidAsync {

    private final boolean forward;

    public FmSearchAsync(PinellService pinellService, boolean forward) {
        super(pinellService);
        this.forward = forward;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (forward) {
            pinellService.searchFMBandForward();
        } else {
            pinellService.searchFMBandRewind();
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
