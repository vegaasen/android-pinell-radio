package com.vegaasen.fun.radio.pinell.async.browse.internet;

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
import com.vegaasen.fun.radio.pinell.async.browse.dab.OnScrollAppendStationsAsync;
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
import java.util.concurrent.TimeUnit;

/**
 * Represents the list of radio stations to browse related to the Internet-mode :-)
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @version 21.01.2016
 * @since 21.01.2016
 */
public class BrowseInternetAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = BrowseInternetAsync.class.getSimpleName();

    private final View view;
    private final WeakReference<BrowseFragment> browseFragmentReference;
    private final ListView radioStationsListView;

    private DeviceCurrentlyPlaying currentlyPlaying;
    private List<RadioStation> radioStations = new ArrayList<>();
    private ProgressBar browseSpinner;

    public BrowseInternetAsync(FragmentManager fragmentManager, View view, ListView radioStationsListView, WeakReference<BrowseFragment> browseFragmentReference, PinellService pinellService) {
        super(fragmentManager, null, pinellService, null);
        this.browseFragmentReference = browseFragmentReference;
        this.view = view;
        this.radioStationsListView = radioStationsListView;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Void doInBackground(Void... voids) {
        currentlyPlaying = pinellService.getCurrentlyPlaying();
        try {
            radioStations.addAll(new GetAllRadioStationsAsync(pinellService).execute().get(TIMEOUT, TimeUnit.SECONDS));
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
        final BrowseStationsActivity adapter = (BrowseStationsActivity) radioStationsListView.getAdapter();
        browseFragment.refreshRadioStationsAndCurrentRadioDataSet(radioStations, currentlyPlaying);
        radioStationsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        radioStationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
//                browseFragment.refreshCurrentRadioDataSet(radioStation);
            }
        });
        if (browseSpinner != null) {
            browseSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void configureViewComponents() {
        browseSpinner = (ProgressBar) view.findViewById(R.id.browseDabSpinner);
    }

}
