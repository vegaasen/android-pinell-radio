package com.vegaasen.fun.radio.pinell.async.browse.fm;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.async.function.GetCurrentPlayingAsync;
import com.vegaasen.fun.radio.pinell.async.function.search.FmSearchAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 25.11.2015
 */
public class BrowseFmAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = BrowseFmAsync.class.getSimpleName();
    private static final float ACTIVE = 0.4f;
    private static final float INACTIVE = 1.0f;

    private final WeakReference<? extends AbstractFragment> currentFragment;
    private final View currentView;
    private final String scanning;

    private DeviceCurrentlyPlaying currentlyPlaying, previousPlaying;
    private TextView fmPlaying, fmTune;
    private ImageButton fmRadioChannelSearchForward, fmRadioChannelSearchRewind;
    private boolean active, scheduled;
    private int playing = R.id.txtFmRadioFrequency, tune = R.id.txtFmRadioCaption, forward = R.id.btnFmRadioForward, rewind = R.id.btnFmRadioRewind;

    public BrowseFmAsync(PinellService pinellService, WeakReference<? extends AbstractFragment> currentFragment, View currentView, String scanning) {
        super(pinellService);
        this.currentFragment = currentFragment;
        this.currentView = currentView;
        this.scanning = scanning;
    }

    public BrowseFmAsync(PinellService pinellService,
                         WeakReference<? extends AbstractFragment> currentFragment,
                         View currentView,
                         String scanning,
                         int playing, int tune, int forward, int rewind) {
        super(pinellService);
        this.currentFragment = currentFragment;
        this.currentView = currentView;
        this.scanning = scanning;
        this.playing = playing;
        this.tune = tune;
        this.forward = forward;
        this.rewind = rewind;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        currentlyPlaying = pinellService.getCurrentlyPlaying();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        AbstractFragment abstractFragment = this.currentFragment.get();
        if (!abstractFragment.isAdded()) {
            Log.d(TAG, "Information-fragment not loaded anymore. Skipping");
            return;
        }
        configureViewComponents();
        if (currentlyPlaying != null) {
            fmPlaying.setText(currentlyPlaying.getName());
            fmTune.setText(currentlyPlaying.getTune());
        }
        fmRadioChannelSearchForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmTune.setText(scanning);
                configureAlpha(fmRadioChannelSearchForward, ACTIVE);
                new FmSearchAsync(pinellService, true).execute();
                triggerFmFrequencyUpdate(fmRadioChannelSearchForward);
            }
        });
        fmRadioChannelSearchRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmTune.setText(scanning);
                configureAlpha(fmRadioChannelSearchRewind, ACTIVE);
                new FmSearchAsync(pinellService, false).execute();
                triggerFmFrequencyUpdate(fmRadioChannelSearchRewind);
            }
        });
    }

    @Override
    protected void configureViewComponents() {
        fmPlaying = (TextView) currentView.findViewById(playing);
        fmTune = (TextView) currentView.findViewById(tune);
        fmRadioChannelSearchForward = (ImageButton) currentView.findViewById(forward);
        fmRadioChannelSearchRewind = (ImageButton) currentView.findViewById(rewind);
    }

    private void triggerFmFrequencyUpdate(final View zeButton) {
        scheduled = false;
        configureScheduledTasks(zeButton);
    }

    private void configureScheduledTasks(final View zeButton) {
        if (!scheduled) {
            active = true;
            final TaskScheduler timer = new TaskScheduler();
            timer.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (active) {
                        Log.d(TAG, "Refreshing FM details");
                        updateFrequency(false);
                        if (currentlyPlaying.equals(previousPlaying)) {
                            configureAlpha(zeButton, INACTIVE);
                            active = false;
                            new TaskScheduler().scheduledAtSpecificTime(new Runnable() {
                                @Override
                                public void run() {
                                    updateFrequency(true);
                                }
                            }, SystemClock.uptimeMillis() + TimeUnit.SECONDS.toMillis(2));
                        }
                    }
                }
            }, 300);
        }
        scheduled = true;
    }

    private void updateFrequency(boolean done) {
        try {
            previousPlaying = currentlyPlaying;
            currentlyPlaying = new GetCurrentPlayingAsync(pinellService).execute().get();
            fmPlaying.setText(currentlyPlaying.getName());
            if (done) {
                fmTune.setText(currentlyPlaying.getTune());
            }
        } catch (Exception e) {
            Log.i(TAG, "Unable to update the frequency");
        }
    }

    private void configureAlpha(final View zeButton, float level) {
        zeButton.setAlpha(level);
    }

}
