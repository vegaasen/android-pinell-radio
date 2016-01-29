package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.async.browse.fm.BrowseFmAsync;
import com.vegaasen.fun.radio.pinell.async.function.IsDeviceOnAsync;
import com.vegaasen.fun.radio.pinell.async.function.UpdateAudioLevelAsync;
import com.vegaasen.fun.radio.pinell.async.playing.NowPlayingAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.model.PinellRadioMode;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * The NowPlayingFragment is a fragment which basically just displays all the details about the currently
 * active source and what is playing right "now".
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 16.11.2015
 * @since 27.5.2015
 */
public class NowPlayingFragment extends AbstractFragment {

    private static final String TAG = NowPlayingFragment.class.getSimpleName();
    private static final long REFRESH_PERIOD = TimeUnit.SECONDS.toMillis(10);
    private static boolean active, scheduled, firstTime = true;

    private View nowPlayingView;
    private SeekBar volumeControl;
    private boolean deviceOn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
//        if (!isWifiEnabledAndConnected()) {
//            nowPlayingView = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//        } else
        deviceOn = false;
        try {
            deviceOn = new IsDeviceOnAsync(getPinellService()).execute().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.d(TAG, "Unable to fetch deviceOn status");
        }
        if (!ApplicationContext.INSTANCE.isPinellDevice()) {
            nowPlayingView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else if (!deviceOn) {
            nowPlayingView = inflater.inflate(R.layout.fragment_now_playing_device_off, container, false);
        } else {
            nowPlayingView = inflater.inflate(invalidRadioMode() ? R.layout.fragment_now_playing_invalid_mode : R.layout.fragment_now_playing, container, false);
            configureComponents();
            configureActions();
            refreshView();
        }
        return nowPlayingView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!isWifiEnabledAndConnected()) {
//            return;
//        }
        if (ApplicationContext.INSTANCE.isPinellDevice() && deviceOn && !invalidRadioMode()) {
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

    private boolean invalidRadioMode() {
        PinellRadioMode activeRadioMode = ApplicationContext.INSTANCE.getActiveRadioMode();
        return activeRadioMode.equals(PinellRadioMode.MUSIC_PLAYER) || activeRadioMode.equals(PinellRadioMode.AUX);
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
                            Log.d(TAG, "Refreshing the now playing details :-)");
                            refreshView();
                        } else {
                            firstTime = false;
                        }
                    }
                }, REFRESH_PERIOD);
            }
            scheduled = true;
        }
    }

    private void refreshView() {
        new NowPlayingAsync(getFragmentManager(), nowPlayingView, new WeakReference<>(this), getPinellService(), getResources().getString(R.string.genericUnknown), invalidRadioMode()).execute();
    }

    private void configureComponents() {
        volumeControl = (SeekBar) nowPlayingView.findViewById(R.id.playingVolumeControlSeek);
    }

    private void configureActions() {
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
        if (!ApplicationContext.INSTANCE.getActiveRadioMode().equals(PinellRadioMode.FM_AM)) {
            View view = nowPlayingView.findViewById(R.id.playingartistChannelSelector);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return;
        }
        Log.d(TAG, "FM seems to be active. Configuring controls");
        new BrowseFmAsync(getPinellService(), new WeakReference<>(this), nowPlayingView, getString(R.string.scanning), R.id.playingRadioChannelTxt, R.id.playingArtistTitleTxt, R.id.btnPlayingFmRadioForward, R.id.btnPlayingFmRadioRewind).execute();
    }

    private void configureVolume(final SeekBar seekBar) {
        if (seekBar != null) {
            int candidateLevel = seekBar.getProgress();
            Log.d(TAG, String.format("Setting AudioLevel to {%s}", candidateLevel));
            new UpdateAudioLevelAsync(getPinellService(), candidateLevel).execute();
        }
    }

}
