package com.vegaasen.fun.radio.pinell.async.browse.dab;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.BrowseFragment;
import com.vegaasen.fun.radio.pinell.adapter.BrowseStationsActivity;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.async.function.GetAllRadioStationsAsync;
import com.vegaasen.fun.radio.pinell.async.function.SetRadioStationAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.http.rest.utils.StringUtils;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the list of radio stations to browse related to the DAB-mode :-)
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @version 22.11.2015
 * @since 22.11.2015
 */
public class BrowseDabAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = BrowseDabAsync.class.getSimpleName();
    private static final boolean SIMPLE = true;

    private final View view;
    private final WeakReference<BrowseFragment> browseFragmentReference;
    private final ListView dabListView;

    private DeviceCurrentlyPlaying currentlyPlaying;
    private List<RadioStation> radioStations = new ArrayList<>();
    private ProgressBar browseDabSpinner;

    public BrowseDabAsync(FragmentManager fragmentManager, View view, ListView dabListView, WeakReference<BrowseFragment> browseFragmentReference, PinellService pinellService) {
        super(fragmentManager, null, pinellService, null);
        this.browseFragmentReference = browseFragmentReference;
        this.view = view;
        this.dabListView = dabListView;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Void doInBackground(Void... voids) {
        currentlyPlaying = pinellService.getCurrentlyPlaying(SIMPLE);
        try {
            radioStations.addAll(new GetAllRadioStationsAsync(pinellService).assembleRadioStations());
        } catch (Exception e) {
            Log.d(TAG, "Unable to fetch radioStations");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        final BrowseFragment browseFragment = browseFragmentReference.get();
        if (!browseFragment.isAdded()) {
            Log.d(TAG, "Fragment removed. Skipping handling");
        }
        configureViewComponents();
        final BrowseStationsActivity adapter = (BrowseStationsActivity) dabListView.getAdapter();
        browseFragment.refreshRadioStationsAndCurrentRadioDataSet(radioStations, currentlyPlaying);
        dabListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int currentLastItem = firstVisibleItem + visibleItemCount;
                if ((currentLastItem == totalItemCount)) {
                    if (browseFragment.getPreviousLastItem() != currentLastItem) {
                        new OnScrollAppendStationsAsync(pinellService, browseFragmentReference).execute();
                        browseFragment.setPreviousLastItem(currentLastItem);
                    }
                }
            }
        });
        dabListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Position {%s} and id {%s} clicked", position, id));
                final RadioStation radioStation = adapter.getItem(position);
                if (currentlyPlaying != null && StringUtils.equalsTrimmed(radioStation.getName(), currentlyPlaying.getName())) {
                    Log.d(TAG, "Skipping the change of radioStation as it seems like the exact same was requested");
                    return;
                }
                if (radioStation.isRadioStationContainer()) {
                    Log.d(TAG, String.format("RadioContainer {%s} selected. Opening the container", radioStation.toString()));
                    browseFragment.refreshRadioStationsDataSet(CollectionUtils.toList(pinellService.enterContainerAndListStations(radioStation)));
                } else {
                    new SetRadioStationAsync(pinellService, radioStation).execute();
                }
                browseFragment.refreshCurrentRadioDataSet(radioStation);
                //new BrowseDabAsync(fragmentManager, view, dabListView, fragmentAdapter, pinellService).execute();
            }
        });
        if (browseDabSpinner != null) {
            browseDabSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void configureViewComponents() {
        browseDabSpinner = (ProgressBar) view.findViewById(R.id.browseDabSpinner);
    }

}
