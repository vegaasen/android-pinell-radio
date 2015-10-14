package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.async.NowPlayingAsync;
import com.vegaasen.fun.radio.pinell.util.ImageUtils;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

import java.util.concurrent.TimeUnit;

/**
 * The NowPlayingFragment is a fragment which basically just displays all the details about the currently
 * active source and what is playing right "now".
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 27.5.2015
 */
public class NowPlayingFragment extends AbstractFragment {

    private static final String TAG = NowPlayingFragment.class.getSimpleName();
    private static boolean active, scheduled, firstTime = true;

    private View nowPlayingView;
    private TextView radioTitle;
    private ImageView radioImage;
    private TextView artistTitle;
    private SeekBar volumeControl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
//        if (!isWifiEnabledAndConnected()) {
//            nowPlayingView = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//        } else
        if (!getPinellService().isPinellDevice()) {
            nowPlayingView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else if (!isDeviceOn()) {
            nowPlayingView = inflater.inflate(R.layout.fragment_now_playing_device_off, container, false);
        } else {
            nowPlayingView = inflater.inflate(R.layout.fragment_now_playing, container, false);
            configureViewComponents();
            configureVolumeController();
            refreshCurrentlyPlaying();
        }
        return nowPlayingView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!isWifiEnabledAndConnected()) {
//            return;
//        }
        if (getPinellService().isPinellDevice()) {
            configureScheduledTasks(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        configureScheduledTasks(false);
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_volume_up_white, R.string.sidebarNowPlaying);
    }

    private void configureScheduledTasks(boolean start) {
        Log.d(TAG, String.format("Running the tasks? {%s}", start));
        active = start;
        if (!scheduled) {
            if (start) {
                TaskScheduler timer = new TaskScheduler();
                timer.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (active && !firstTime) {
                            Log.d(TAG, "Refreshing the information");
                            populateComponentInformation();
                        } else {
                            firstTime = false;
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(25));
            }
            scheduled = true;
        }
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

    private void refreshCurrentlyPlaying() {
        NowPlayingAsync nowPlayingAsync = new NowPlayingAsync(nowPlayingView, getPinellService(), getResources().getString(R.string.genericUnknown));
        nowPlayingAsync.execute();
    }

    private void populateComponentInformation() {
        if (!componentsAvailable()) {
            return;
        }
        DeviceCurrentlyPlaying deviceCurrentlyPlaying = getPinellService().getCurrentlyPlaying();
        if (deviceCurrentlyPlaying == null) {
            Log.w(TAG, "Unable to fetch currently playing. Device not turned on or not a Pinell device?");
            return;
        }
        radioTitle.setText(Strings.isNullOrEmpty(deviceCurrentlyPlaying.getName()) ? getResources().getString(R.string.genericUnknown) : deviceCurrentlyPlaying.getName());
        artistTitle.setText(Strings.isNullOrEmpty(deviceCurrentlyPlaying.getTune()) ? getResources().getString(R.string.genericUnknown) : deviceCurrentlyPlaying.getTune());
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

    private boolean componentsAvailable() {
        return radioTitle != null;
    }

    private boolean isDeviceOn() {
        return getPinellService().isPoweredOn();
    }

}
