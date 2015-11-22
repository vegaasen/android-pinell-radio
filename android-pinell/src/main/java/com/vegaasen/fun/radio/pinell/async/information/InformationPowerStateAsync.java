package com.vegaasen.fun.radio.pinell.async.information;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @see com.vegaasen.fun.radio.pinell.activity.fragment.functions.InformationFragment
 */
public class InformationPowerStateAsync extends AbstractFragmentVoidAsync {

    private PowerState powerState;

    public InformationPowerStateAsync(PinellService pinellService, PowerState powerState) {
        super(pinellService);
        this.powerState = powerState;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        pinellService.setPowerState(powerState);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    @Override
    protected void configureViewComponents() {

    }
}
