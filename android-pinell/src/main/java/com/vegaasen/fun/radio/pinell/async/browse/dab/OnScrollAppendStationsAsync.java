package com.vegaasen.fun.radio.pinell.async.browse.dab;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.BrowseFragment;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Todo: this is not triggered for some odd reason. Figure it out.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class OnScrollAppendStationsAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = OnScrollAppendStationsAsync.class.getSimpleName();

    private final WeakReference<BrowseFragment> weakReference;

    public OnScrollAppendStationsAsync(PinellService pinellService, WeakReference<BrowseFragment> fragmentWeakReference) {
        super(pinellService);
        weakReference = fragmentWeakReference;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "Attempting to update radioStations");
        BrowseFragment browseFragment = weakReference.get();
        if (!browseFragment.isLoadedAll()) {
            List<RadioStation> loadedRadioStations = browseFragment.getLoadedRadioStations();
            List<RadioStation> radioStations = assembleRadioStations(loadedRadioStations.size());
            if (!CollectionUtils.isEmpty(radioStations)) {
                CollectionUtils.addWithoutDuplicates(loadedRadioStations, radioStations);
                browseFragment.refreshDabSimpleDataSet(loadedRadioStations);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void configureViewComponents() {
    }

    private List<RadioStation> assembleRadioStations(int from) {
        return sortRadioStations(CollectionUtils.toList(pinellService.listRadioStations(from)));
    }

    private List<RadioStation> sortRadioStations(final List<RadioStation> radioStations) {
        Collections.sort(radioStations, new Comparators.RadioStationsComparator());
        return radioStations;
    }

}
