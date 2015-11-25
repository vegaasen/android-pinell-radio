package com.vegaasen.fun.radio.pinell.async.function;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class GetCurrentPlayingAsync extends AbstractFragmentAsync<DeviceCurrentlyPlaying> {

    public GetCurrentPlayingAsync(PinellService pinellService) {
        super(pinellService);
    }

    @Override
    protected DeviceCurrentlyPlaying doInBackground(DeviceCurrentlyPlaying... deviceCurrentlyPlayings) {
        return pinellService.getCurrentlyPlaying();
    }

    @Override
    protected void onPostExecute(DeviceCurrentlyPlaying aVoid) {
    }

    @Override
    protected void configureViewComponents() {
    }
}
