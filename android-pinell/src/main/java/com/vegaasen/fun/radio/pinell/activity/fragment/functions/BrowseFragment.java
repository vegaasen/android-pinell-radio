package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.adapter.BrowseStationsActivity;
import com.vegaasen.fun.radio.pinell.async.browse.dab.BrowseDabAsync;
import com.vegaasen.fun.radio.pinell.async.browse.fm.BrowseFmAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents the browsing features of the Pinell device. This is only related to the various radio stations available.
 * It does not support FM tuning
 * <p/>
 * Browsing features supported
 * - DAB
 * - Internet Radio
 * - FM presets
 * <p/>
 * TODO: Implement searching possibilities which updates async on searching (based on Mhz etc)
 * TODO: See how Internet Radio works
 * FIXME: Performance-wise, this thing sucks. Should be fixed
 * FIXME: When loading more channels (DAB, Internet-mode), this must be illustrated somehow. Figure it out!
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 24.11.2015
 * @since 27.5.2015
 */
public class BrowseFragment extends AbstractFragment {

    private static final String TAG = BrowseFragment.class.getSimpleName();
    private static final long REFRESH_PERIOD = TimeUnit.SECONDS.toMillis(10);
    private static boolean active, scheduled, firstTime = true;

    private View browseFragment;
    private List<RadioStation> loadedRadioStations = new ArrayList<>();
    private int previousLastItem;

    private ListView stationsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
//        if (!isWifiEnabledAndConnected()) {
//            browseFragment = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//        } else
        if (!ApplicationContext.INSTANCE.isPinellDevice()) {
            browseFragment = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            switch (ApplicationContext.INSTANCE.getActiveRadioMode()) {
                case DAB:
                    configureViewDAB(inflater, container);
                    break;
                case FM_AM:
                    configureViewFM(inflater, container);
                    break;
                case INTERNET_RADIO:
                    configureViewInternetRadio(inflater, container);
                    break;
                case MUSIC_PLAYER:
                case AUX:
                    configureViewUnsupported(inflater, container);
                    break;
                case UNKNOWN:
                default:
                    configureViewUnknown(inflater, container);
            }
        }
        return browseFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!isWifiEnabledAndConnected()) {
//            return;
//        }
        if (ApplicationContext.INSTANCE.isPinellDevice()) {
            configureScheduledTasks(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        configureScheduledTasks(false);
    }

    private void configureScheduledTasks(boolean start) {
        active = start;
        if (!scheduled) {
            if (start) {
                TaskScheduler timer = new TaskScheduler();
                timer.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (active && !firstTime) {
                            switch (ApplicationContext.INSTANCE.getActiveRadioMode()) {
                                case FM_AM:
                                    refreshFm();
                                    break;
                                case DAB:
                                case INTERNET_RADIO:
                                case MUSIC_PLAYER:
                                case AUX:
                                case UNKNOWN:
                                default:
                            }
                        } else {
                            firstTime = false;
                        }
                    }
                }, REFRESH_PERIOD);
            }
            scheduled = true;
        }
    }

    public void refreshDabSimpleDataSet(List<RadioStation> radioStations) {
        BrowseStationsActivity adapter = (BrowseStationsActivity) stationsListView.getAdapter();
        adapter.updateRadioStations(radioStations);
        loadedRadioStations = radioStations;
        adapter.notifyDataSetChanged();
    }

    public void refreshDabSimpleDataSet(List<RadioStation> radioStations, DeviceCurrentlyPlaying currentlyPlaying) {
        BrowseStationsActivity adapter = (BrowseStationsActivity) stationsListView.getAdapter();
        adapter.updateRadioStations(radioStations);
        adapter.updateCurrentRadioStation(currentlyPlaying);
        setLoadedRadioStations(radioStations);
        adapter.notifyDataSetChanged();
    }

    public void refreshDabDataSet(RadioStation currentRadioStation) {
        ApplicationContext.INSTANCE.setActiveRadioStation(currentRadioStation);
        BrowseStationsActivity adapter = (BrowseStationsActivity) stationsListView.getAdapter();
        adapter.updateCurrentRadioStation(currentRadioStation);
        adapter.notifyDataSetChanged();
    }

    public int getPreviousLastItem() {
        return previousLastItem;
    }

    public void setPreviousLastItem(int previousLastItem) {
        this.previousLastItem = previousLastItem;
    }

    public List<RadioStation> getLoadedRadioStations() {
        return loadedRadioStations;
    }

    public void setLoadedRadioStations(List<RadioStation> loadedRadioStations) {
        this.loadedRadioStations = loadedRadioStations;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_queue_music_white, R.string.sidebarBrowse);
    }

    private void configureViewUnknown(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_unknown, container, false);
    }

    private void configureViewUnsupported(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_unsupported, container, false);
        TextView txtReason = (TextView) browseFragment.findViewById(R.id.txtInputSourceNAConnectedReason);
        txtReason.setText(String.format(getString(R.string.inputSourceNotAvailableReason), ApplicationContext.INSTANCE.getActiveRadioMode().getName()));
        Log.d(TAG, "Unsupported configured");
    }

    private void configureViewDAB(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_dab, container, false);
        CollectionUtils.clear(loadedRadioStations);
        stationsListView = (ListView) browseFragment.findViewById(R.id.browseListOfStations);
        stationsListView.setAdapter(new BrowseStationsActivity(browseFragment.getContext(), loadedRadioStations));
        new BrowseDabAsync(getFragmentManager(), browseFragment, stationsListView, new WeakReference<>(this), getPinellService()).execute();
        Log.d(TAG, "DAB configured");
    }

    private void configureViewFM(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_fm, container, false);
        refreshFm();
        Log.d(TAG, "FM configured");
    }

    private void configureViewInternetRadio(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_internet, container, false);
        CollectionUtils.clear(loadedRadioStations);
        stationsListView = (ListView) browseFragment.findViewById(R.id.browseListOfStations);
        //todo: can the dab/internet-view be merged together?
        stationsListView.setAdapter(new BrowseStationsActivity(browseFragment.getContext(), loadedRadioStations));
        new BrowseDabAsync(getFragmentManager(), browseFragment, stationsListView, new WeakReference<>(this), getPinellService()).execute();
        Log.d(TAG, "Internet configured");
    }

    private void refreshFm() {
        Log.d(TAG, "Refreshing the FM details :-)");
        new BrowseFmAsync(getPinellService(), new WeakReference<>(this), browseFragment, getString(R.string.scanning)).execute();
    }

}
