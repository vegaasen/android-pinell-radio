package com.vegaasen.fun.radio.pinell.async.playing;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.NowPlayingFragment;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.ImageUtils;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

import java.lang.ref.WeakReference;

/**
 * Simple nowPlaying asyncronious task. This should help on making the application more snappy,
 * as there will be a spinner showing whilst loading the application component representing the Now Playing
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 0.1-SNAPSHOT
 * @see com.vegaasen.fun.radio.pinell.activity.fragment.functions.NowPlayingFragment
 * @since 13.09.2015
 */
public class NowPlayingAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = NowPlayingAsync.class.getSimpleName();

    private final WeakReference<NowPlayingFragment> nowPlayingFragment;

    private DeviceCurrentlyPlaying deviceCurrentlyPlaying;
    private DeviceAudio audioLevels;
    private TextView radioTitle;
    private ImageView radioImage;
    private TextView artistTitle;
    private SeekBar volumeControl;
    private ProgressBar progressBar;

    public NowPlayingAsync(FragmentManager fragmentManager, View view, WeakReference<NowPlayingFragment> nowPlayingFragment, PinellService pinellService, String unknown) {
        super(fragmentManager, view, pinellService, unknown);
        this.nowPlayingFragment = nowPlayingFragment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        deviceCurrentlyPlaying = pinellService.getCurrentlyPlaying();
        audioLevels = pinellService.getAudioLevels();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        NowPlayingFragment nowPlayingFragment = this.nowPlayingFragment.get();
        if (!nowPlayingFragment.isAdded()) {
            Log.d(TAG, "Fragment removed. Skipping handling");
            return;
        }
        configureViewComponents();
        if (deviceCurrentlyPlaying == null) {
            Log.w(TAG, "Unable to fetch currently playing. Device not turned on or not a Pinell device?");
            return;
        }
        if (radioTitle == null || artistTitle == null || volumeControl == null) {
            Log.d(TAG, "Unable to process the details as the view does not seems to exist?");
            return;
        }
        radioTitle.setText(Strings.isNullOrEmpty(deviceCurrentlyPlaying.getName()) ? unknown : deviceCurrentlyPlaying.getName());
        artistTitle.setText(Strings.isNullOrEmpty(deviceCurrentlyPlaying.getTune()) ? unknown : deviceCurrentlyPlaying.getTune());
        if (!Strings.isNullOrEmpty(deviceCurrentlyPlaying.getGraphicsUri())) {
            radioImage.setBackground(ImageUtils.convert(deviceCurrentlyPlaying.getGraphicsUri()));
        }
        if (audioLevels == null) {
            Log.w(TAG, "For some reason, the AudioLevel could not be obtained - disabling the volumeControl");
            volumeControl.setEnabled(false);
            return;
        }
        volumeControl.setProgress(audioLevels.getLevel());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void configureViewComponents() {
        progressBar = (ProgressBar) view.findViewById(R.id.informationProgressBar);
        radioTitle = (TextView) view.findViewById(R.id.playingRadioChannelTxt);
        radioImage = (ImageView) view.findViewById(R.id.playingRadioChannelImg);
        artistTitle = (TextView) view.findViewById(R.id.playingArtistTitleTxt);
        volumeControl = (SeekBar) view.findViewById(R.id.playingVolumeControlSeek);
    }

}
