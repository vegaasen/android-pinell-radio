package com.vegaasen.fun.radio.pinell.async.function;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

/**
 * Update the RadioMode async
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @see com.vegaasen.fun.radio.pinell.activity.MainActivity
 * @see com.vegaasen.fun.radio.pinell.activity.fragment.functions.InputSourceFragment
 */
public class UpdateRadioModeAsync extends AbstractFragmentVoidAsync {

    private RadioMode currentInputSource;

    public UpdateRadioModeAsync(PinellService pinellService) {
        super(pinellService);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        currentInputSource = pinellService.getCurrentInputSource();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ApplicationContext.INSTANCE.setActiveRadioMode(currentInputSource);
        ApplicationContext.INSTANCE.setRadioConnected(true);
    }

    @Override
    protected void configureViewComponents() {

    }
}
