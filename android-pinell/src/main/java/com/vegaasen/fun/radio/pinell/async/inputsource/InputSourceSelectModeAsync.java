package com.vegaasen.fun.radio.pinell.async.inputsource;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

/**
 * In order to simplify the change of active radio-modes, use this class implementation
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 22.11.2015
 */
public class InputSourceSelectModeAsync extends AbstractFragmentVoidAsync {

    private final RadioMode selectedRadioMode;

    public InputSourceSelectModeAsync(PinellService pinellService, RadioMode selectedRadioMode) {
        super(pinellService);
        this.selectedRadioMode = selectedRadioMode;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (selectedRadioMode != null) {
            pinellService.setInputSource(selectedRadioMode);
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
