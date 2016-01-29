package com.vegaasen.fun.radio.pinell.async.function.browse;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

import java.util.Collections;
import java.util.List;

/**
 * Fetches the content of a specific radioStation. The radioStation is actually defined as a Folder/Container,
 * so this method will help on fetching the contents of that candidate-container.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 23.01.2016
 */
public class GetContainerContentAsync extends AbstractFragmentAsync<List<RadioStation>> {

    private static final String TAG = GetContainerContentAsync.class.getSimpleName();

    private final RadioStation container;

    public GetContainerContentAsync(PinellService pinellService, RadioStation container) {
        super(pinellService);
        this.container = container;
    }

    @Override
    @SafeVarargs
    protected final List<RadioStation> doInBackground(List<RadioStation>... lists) {
        if (container == null) {
            return Collections.emptyList();
        }
        Log.d(TAG, String.format("Fetching container content for {%s}", container));
        return CollectionUtils.toList(pinellService.enterContainerAndListStations(container));
    }

    @Override
    protected void onPostExecute(List<RadioStation> aVoid) {
    }

    @Override
    protected void configureViewComponents() {
    }
}
