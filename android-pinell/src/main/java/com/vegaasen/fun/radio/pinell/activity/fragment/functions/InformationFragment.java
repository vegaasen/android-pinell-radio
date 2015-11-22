package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.async.information.InformationLoadingAsync;
import com.vegaasen.fun.radio.pinell.async.information.InformationSoundLevelAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * Representation of the information regarding the selected device is represented through this fragment
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 22.11.2015
 * @since 29.03.2015
 */
public class InformationFragment extends AbstractFragment {

    private static final String TAG = InformationFragment.class.getSimpleName();

    private static boolean active, scheduled;

    private View informationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
        //        if (!isWifiEnabledAndConnected()) {
//            informationView = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//        } else
        //todo: This checks too quick (before it has been set) - what to do here on first entry? Pass to another screen?
        if (!ApplicationContext.INSTANCE.isPinellDevice()) {
            informationView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            informationView = inflater.inflate(R.layout.fragment_information, container, false);
            refreshView();
        }
        return informationView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ApplicationContext.INSTANCE.isPinellDevice()) {
            configureScheduledTasks(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        configureScheduledTasks(false);
    }

    public void updateSoundLevel() {
        new InformationSoundLevelAsync(getFragmentManager(), informationView, getPinellService(), getResources().getString(R.string.genericUnknown));
    }

    public void refreshView() {
        if (informationView == null) {
            Log.w(TAG, "Unable to refresh the informationView, as it seems to be nilled for some reason");
            return;
        }
        new InformationLoadingAsync(getFragmentManager(), new WeakReference<>(this), informationView, getPinellService(), getResources().getString(R.string.genericUnknown)).execute();
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_radio_white, R.string.sidebarInformation);
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
                        if (active) {
                            Log.d(TAG, "Refreshing the information");
                            refreshView();
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(25));
            }
            scheduled = true;
        }
    }

}
