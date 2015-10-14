package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.adapter.BrowseStationsActivity;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

import java.util.Collections;
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
 * FIXME: Not able to see more than n-number of radio stations..what is going on :-)?
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 30.8.2015
 * @since 27.5.2015
 */
public class BrowseFragment extends AbstractFragment {

    private static final String TAG = BrowseFragment.class.getSimpleName();
    public static final float ACTIVE = 0.4f;
    public static final float INACTIVE = 1.0f;

    private static boolean active, scheduled;

    private View browseFragment;
    private List<RadioStation> loadedRadioStations;
    private DeviceCurrentlyPlaying currentlyPlaying, previousPlaying;
    private TextView fmPlaying, fmTune;
    private ImageButton fmRadioChannelSearchForward, fmRadioChannelSearchRewind;
    private int previousLastItem;
    private boolean loadedAll, configured;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
//        if (!isWifiEnabledAndConnected()) {
//            browseFragment = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//        } else
        if (!getPinellService().isPinellDevice()) {
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
    }

    private void configureViewDAB(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_dab, container, false);
        CollectionUtils.clear(loadedRadioStations);
        listRadioStationsAvailableForDAB();
    }

    private void configureViewFM(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_fm, container, false);
        configureFMComponents();
    }

    private void configureViewInternetRadio(LayoutInflater inflater, ViewGroup container) {
        browseFragment = inflater.inflate(R.layout.fragment_browse_unsupported, container, false);
        TextView txtReason = (TextView) browseFragment.findViewById(R.id.txtInputSourceNAConnectedReason);
        txtReason.setText("Oops. This is currently not supported");
    }

    private void configureFMComponents() {
        DeviceCurrentlyPlaying currentlyPlaying = getPinellService().getCurrentlyPlaying();
        if (currentlyPlaying != null) {
            fmPlaying = (TextView) browseFragment.findViewById(R.id.txtFmRadioFrequency);
            fmTune = (TextView) browseFragment.findViewById(R.id.txtFmRadioCaption);
            fmPlaying.setText(currentlyPlaying.getName());
            fmTune.setText(currentlyPlaying.getTune());
        }
        fmRadioChannelSearchForward = (ImageButton) browseFragment.findViewById(R.id.btnFmRadioForward);
        fmRadioChannelSearchRewind = (ImageButton) browseFragment.findViewById(R.id.btnFmRadioRewind);
        fmRadioChannelSearchForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmTune.setText(getString(R.string.scanning));
                configureAlpha(fmRadioChannelSearchForward, ACTIVE);
                getPinellService().searchFMBandForward();
                triggerFmFrequencyUpdate(fmRadioChannelSearchForward);
            }
        });
        fmRadioChannelSearchRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmTune.setText(getString(R.string.scanning));
                configureAlpha(fmRadioChannelSearchRewind, ACTIVE);
                getPinellService().searchFMBandRewind();
                triggerFmFrequencyUpdate(fmRadioChannelSearchRewind);
            }
        });
    }

    private void triggerFmFrequencyUpdate(final View zeButton) {
        active = true;
        configureScheduledTasks(zeButton);
    }

    private void configureScheduledTasks(final View zeButton) {
        if (!scheduled) {
            final TaskScheduler timer = new TaskScheduler();
            timer.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (active) {
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
            }, 400);
        }
        scheduled = true;
    }

    private void configureAlpha(final View zeButton, float level) {
        zeButton.setAlpha(level);
    }

    private void updateFrequency(boolean done) {
        previousPlaying = currentlyPlaying;
        currentlyPlaying = getPinellService().getCurrentlyPlaying();
        fmPlaying.setText(currentlyPlaying.getName());
        if (done) {
            fmTune.setText(currentlyPlaying.getTune());
        }
    }

    private void listRadioStationsAvailableForDAB() {
        if (browseFragment == null) {
            Log.w(TAG, "Unable to list radioStations, as the browseFragment is not available");
            return;
        }
        final ListView radioStationsOverview = (ListView) browseFragment.findViewById(R.id.browseListOfStations);
        if (radioStationsOverview == null) {
            Log.w(TAG, "It seems like the radioStationsOverview is nilled, skipping");
            return;
        }
        final BrowseStationsActivity adapter = getRadioStationsActivity(radioStationsOverview);
        adapter.updateCurrentRadioStation(getPinellService().getCurrentlyPlaying());
        if (!configured) {
            radioStationsOverview.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int currentLastItem = firstVisibleItem + visibleItemCount;
                    if ((currentLastItem == totalItemCount)) {
                        if (previousLastItem != currentLastItem) {
                            appendRadioStationsForDAB(adapter);
                            previousLastItem = currentLastItem;
                        }
                    }
                }
            });
            radioStationsOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, String.format("Position {%s} and id {%s} clicked", position, id));
                    final RadioStation radioStation = adapter.getItem(position);
                    if (radioStation.isRadioStationContainer()) {
                        Log.d(TAG, String.format("RadioContainer {%s} selected. Opening the container", radioStation.toString()));
                        loadedRadioStations = CollectionUtils.toList(getPinellService().enterContainerAndListStations(radioStation));
                    } else {
                        Log.d(TAG, String.format("RadioStation {%s} selected. Switching to this station", radioStation.toString()));
                        getPinellService().setRadioStation(radioStation);
                    }
                    listRadioStationsAvailableForDAB();
                }
            });
        }
        configured = true;
    }

    private BrowseStationsActivity getRadioStationsActivity(ListView overview) {
        BrowseStationsActivity adapter;
        loadedRadioStations = CollectionUtils.isEmpty(loadedRadioStations) ? assembleRadioStations() : loadedRadioStations;
        if (overview.getAdapter() == null) {
            adapter = new BrowseStationsActivity(browseFragment.getContext(), loadedRadioStations);
            overview.setAdapter(adapter);
        } else {
            adapter = (BrowseStationsActivity) overview.getAdapter();
            adapter.updateRadioStations(CollectionUtils.copy(loadedRadioStations));
            adapter.notifyDataSetChanged();
        }
        return adapter;
    }

    private void appendRadioStationsForDAB(BrowseStationsActivity adapter) {
        if (adapter != null && !loadedAll) {
            List<RadioStation> candidates = assembleRadioStations(loadedRadioStations.size());
            if (!CollectionUtils.isEmpty(candidates)) {
                CollectionUtils.addWithoutDuplicates(loadedRadioStations, candidates);
                adapter.notifyDataSetChanged();
            } else {
                loadedAll = true;
            }
        }
    }

    private List<RadioStation> assembleRadioStations() {
        return assembleRadioStations(0);
    }

    private List<RadioStation> assembleRadioStations(int from) {
        return sortRadioStations(CollectionUtils.toList(getPinellService().listRadioStations(from)));
    }

    private List<RadioStation> sortRadioStations(final List<RadioStation> radioStations) {
        Collections.sort(radioStations, new Comparators.RadioStationsComparator());
        return radioStations;
    }

}
