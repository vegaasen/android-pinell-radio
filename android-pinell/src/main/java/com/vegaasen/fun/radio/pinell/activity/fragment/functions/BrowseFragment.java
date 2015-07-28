package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.adapter.BrowseStationsActivity;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

import java.util.Collections;
import java.util.List;

/**
 * Represents the browsing features of the Pinell device. This is only related to the various radio stations available.
 * It does not support FM tuning
 * <p/>
 * Browsing features supported
 * - DAB
 * - Internet Radio
 * - FM presets
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 26.7.2015
 * @since 27.5.2015
 */
public class BrowseFragment extends AbstractFragment {

    private static final String TAG = BrowseFragment.class.getSimpleName();

    private View browseFragment;
    private List<RadioStation> loadedRadioStations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        browseFragment = inflater.inflate(R.layout.fragment_browse, container, false);
        if (browseFragment == null) {
            Log.e(TAG, "For some reason, the view were unable to be found. Dying");
            throw new RuntimeException("Missing required view in the initialization of the application");
        }
        changeActiveContent(container);
        listRadioStationsAvailable();
        return browseFragment;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_queue_music_black, R.string.sidebarBrowse);
    }

    private void listRadioStationsAvailable() {
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
                listRadioStationsAvailable();
            }
        });
        final Button loadMoreItemsButton = (Button) browseFragment.findViewById(R.id.browseButtonLoadMore);
        loadMoreItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "haha");
                if (loadedRadioStations != null) {
                    loadedRadioStations.addAll(assembleRadioStations(loadedRadioStations.size() - 1));
                    listRadioStationsAvailable();
                }
            }
        });
    }

    private BrowseStationsActivity getRadioStationsActivity(ListView overview) {
        BrowseStationsActivity adapter;
        final List<RadioStation> radioStations = CollectionUtils.isEmpty(loadedRadioStations) ? assembleRadioStations() : loadedRadioStations;
        if (overview.getAdapter() == null) {
            adapter = new BrowseStationsActivity(browseFragment.getContext(), radioStations);
            overview.setAdapter(adapter);
        } else {
            adapter = (BrowseStationsActivity) overview.getAdapter();
            adapter.updateRadioStations(radioStations);
            adapter.notifyDataSetChanged();
        }
        loadedRadioStations = radioStations;
        return adapter;
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
