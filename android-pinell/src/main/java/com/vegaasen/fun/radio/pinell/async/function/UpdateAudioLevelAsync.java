package com.vegaasen.fun.radio.pinell.async.function;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;

/**
 * Async task to update audioLevels asyncronious. Can be used with either +1/-1 or to set the (level).
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class UpdateAudioLevelAsync extends AbstractFragmentVoidAsync {

    private static final int UNDEFINED = -42;

    private final boolean up;
    private final int level;

    private DeviceAudio audioLevels;

    public UpdateAudioLevelAsync(PinellService pinellService, boolean up) {
        super(pinellService);
        this.up = up;
        this.level = UNDEFINED;
    }

    public UpdateAudioLevelAsync(PinellService pinellService, int level) {
        super(pinellService);
        this.level = level;
        this.up = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int candidate = 0;
        if (level == UNDEFINED) {
            audioLevels = pinellService.getAudioLevels();
            final int candidateLevel = audioLevels.getLevel();
            candidate = up ?
                    candidateLevel <= 38 ? candidateLevel + 1 : candidateLevel :
                    candidateLevel > 0 ? candidateLevel - 1 : candidateLevel;
        } else {
            candidate = level;
        }
        if (candidate == 0) {
            pinellService.setAudioMuted();
        } else {
            pinellService.setAudioLevel(candidate);
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
