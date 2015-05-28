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
        nowPlayingView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        if (nowPlayingView == null) {
            Log.e(TAG, "For some reason, the view were unable to be found. Dying");
            throw new RuntimeException("Missing required view in the initialization of the application");
        }
        configureViewComponents();
        configureVolumeController();
        refreshCurrentlyPlaying();
        return nowPlayingView;
    }

    private void configureViewComponents() {
        radioTitle = (TextView) nowPlayingView.findViewById(R.id.playingRadioChannelTxt);
        radioImage = (ImageView) nowPlayingView.findViewById(R.id.playingRadioChannelImg);
        artistTitle = (TextView) nowPlayingView.findViewById(R.id.playingArtistTitleTxt);
        volumeControl = (SeekBar) nowPlayingView.findViewById(R.id.playingVolumeControlSeek);
    }

    private void configureVolumeController() {
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "Progress changed..?");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Started tracking touches");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Stopped tracking touches");
            }
        });
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
        radioTitle.setText(deviceCurrentlyPlaying.getName());
        artistTitle.setText(deviceCurrentlyPlaying.getTune());
        radioImage.setBackground(ImageUtils.convert(deviceCurrentlyPlaying.getGraphicsUri()));
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
