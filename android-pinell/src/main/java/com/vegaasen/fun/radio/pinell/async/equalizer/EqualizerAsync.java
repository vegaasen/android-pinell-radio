package com.vegaasen.fun.radio.pinell.async.equalizer;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.EqualizerFragment;
import com.vegaasen.fun.radio.pinell.adapter.EqualizerAdapter;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Simple Equalizer async task. This should help on making the application more snappy,
 * as there will be a spinner showing whilst loading the application component representing the Equalizer-component
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 0.1-SNAPSHOT
 * @see com.vegaasen.fun.radio.pinell.activity.fragment.functions.EqualizerFragment
 * @since 16.11.2015
 */
public class EqualizerAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = EqualizerAsync.class.getSimpleName();

    private final View view;
    private final WeakReference<EqualizerFragment> adapter;
    private final ListView equalizerOverview;

    private ProgressBar spinner;
    private List<Equalizer> equalizers;
    private Equalizer currentEqualizer;

    public EqualizerAsync(FragmentManager fragmentManager, View view, ListView equalizerOverview, WeakReference<EqualizerFragment> adapter, PinellService pinellService) {
        super(fragmentManager, null, pinellService, null);
        this.adapter = adapter;
        this.view = view;
        this.equalizerOverview = equalizerOverview;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        configureViewComponents();
        if (view == null) {
            Log.w(TAG, "It seems like the equalizerOverview is nilled, skipping");
            return null;
        }
        currentEqualizer = pinellService.getCurrentEqualizer();
        equalizers = getEqualizers();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        final EqualizerAdapter equalizerAdapter = (EqualizerAdapter) equalizerOverview.getAdapter();
        equalizerOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Position {%s} and id {%s} clicked", position, id));
                final Equalizer selectedEqualizer = equalizerAdapter.getItem(position);
                new EqualizerSelectModeAsync(pinellService, selectedEqualizer).execute();
                // Perform a refresh of the list of available equalizers
                new EqualizerAsync(fragmentManager, view, equalizerOverview, adapter, pinellService).execute();
            }
        });
        if (spinner != null) {
            spinner.setVisibility(View.GONE);
        }
        adapter.get().refreshDataSet(equalizers, currentEqualizer);
    }

    @Override
    protected void configureViewComponents() {
        spinner = (ProgressBar) view.findViewById(R.id.equalizerSpinner);
    }

    private List<Equalizer> getEqualizers() {
        final List<Equalizer> equalizers = CollectionUtils.toList(pinellService.listEqualizers());
        Collections.sort(equalizers, new Comparators.EqualizerComparator());
        return equalizers;
    }

}
