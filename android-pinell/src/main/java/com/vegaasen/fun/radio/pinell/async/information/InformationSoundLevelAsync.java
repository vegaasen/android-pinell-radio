package com.vegaasen.fun.radio.pinell.async.information;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @see com.vegaasen.fun.radio.pinell.activity.fragment.functions.InformationFragment
 */
public class InformationSoundLevelAsync extends AbstractFragmentVoidAsync {

    private TextView currentSoundLevel;
    private DeviceAudio audioLevels;

    public InformationSoundLevelAsync(FragmentManager fragmentManager, View informationView, PinellService pinellService, String unknown) {
        super(fragmentManager, informationView, pinellService, unknown);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        audioLevels = pinellService.getAudioLevels();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        configureViewComponents();
        currentSoundLevel.setText(audioLevels == null ? unknown : String.format("%s of %s", Integer.toString(audioLevels.getLevel()), "32")); // getString(R.integer.volumeControlMax)
    }

    @Override
    protected void configureViewComponents() {
        currentSoundLevel = (TextView) view.findViewById(R.id.informationVolumeLevelTxt);
    }
}
