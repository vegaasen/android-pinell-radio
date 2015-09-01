package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Strings;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.util.ImageUtils;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;

/**
 * The NowPlayingFragment is a fragment which basically just displays all the details about the currently
 * active source and what is playing right "now".
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 27.5.2015
 */
public class NowPlayingFragment extends AbstractFragment {

    private static final String TAG = NowPlayingFragment.class.getSimpleName();

    private View nowPlayingView;
    private TextView radioTitle;
    private ImageView radioImage;
    private TextView artistTitle;
    private SeekBar volumeControl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
        if (!getPinellService().isPinellDevice()) {
            nowPlayingView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            nowPlayingView = inflater.inflate(R.layout.fragment_now_playing, container, false);
            if (nowPlayingView == null) {
                Log.e(TAG, "For some reason, the view were unable to be found. Dying");
                throw new RuntimeException("Missing required view in the initialization of the application");
            }
            configureViewComponents();
            configureVolumeController();
            refreshCurrentlyPlaying();
        }
        return nowPlayingView;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_volume_up_white, R.string.sidebarNowPlaying);
    }

    private void configureViewComponents() {
        radioTitle = (TextView) nowPlayingView.findViewById(R.id.playingRadioChannelTxt);
        radioImage = (ImageView) nowPlayingView.findViewById(R.id.playingRadioChannelImg);
        artistTitle = (TextView) nowPlayingView.findViewById(R.id.playingArtistTitleTxt);
        volumeControl = (SeekBar) nowPlayingView.findViewById(R.id.playingVolumeControlSeek);
    }

    //todo: the rest
    private void configureVolumeController() {
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                configureVolume(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                configureVolume(seekBar);
            }
        });
    }

    private void configureVolume(final SeekBar seekBar) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (seekBar != null) {
                    int candidateLevel = seekBar.getProgress();
                    if (candidateLevel == 0) {
                        getPinellService().setAudioMuted();
                        return;
                    }
                    getPinellService().setAudioLevel(candidateLevel);
                    Log.d(TAG, String.format("AudioLevel set to {%s}", candidateLevel));
                }
            }
        }).run();
    }

    //todo: this might be better to put into a separate Thread.
    private void refreshCurrentlyPlaying() {
        if (!getPinellService().isPinellDevice()) {
            return;
        }
        assertTurnedOn();
        populateComponentInformation();
    }

    private void populateComponentInformation() {
        DeviceCurrentlyPlaying deviceCurrentlyPlaying = getPinellService().getCurrentlyPlaying();
        if (deviceCurrentlyPlaying == null) {
            Log.w(TAG, "Unable to fetch currently playing. Device not turned on or not a Pinell device?");
            return;
        }
        radioTitle.setText(Strings.isNullOrEmpty(deviceCurrentlyPlaying.getName()) ? getResources().getString(R.string.genericUndocumented) : deviceCurrentlyPlaying.getName());
        artistTitle.setText(Strings.isNullOrEmpty(deviceCurrentlyPlaying.getTune()) ? getResources().getString(R.string.genericUndocumented) : deviceCurrentlyPlaying.getTune());
        if (!Strings.isNullOrEmpty(deviceCurrentlyPlaying.getGraphicsUri())) {
            radioImage.setBackground(ImageUtils.convert(deviceCurrentlyPlaying.getGraphicsUri()));
        }
        final DeviceAudio audioLevels = getPinellService().getAudioLevels();
        if (audioLevels == null) {
            Log.w(TAG, "For some reason, the AudioLevel could not be obtained - disabling the volumeControl");
            volumeControl.setEnabled(false);
            return;
        }
        volumeControl.setProgress(audioLevels.getLevel());
    }

    private void assertTurnedOn() {
        if (!getPinellService().isPoweredOn()) {
            Toast.makeText(nowPlayingView.getContext(), R.string.messagePinellDeviceNotOn, Toast.LENGTH_SHORT).show();
            getPinellService().setPowerState(PowerState.ON);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
