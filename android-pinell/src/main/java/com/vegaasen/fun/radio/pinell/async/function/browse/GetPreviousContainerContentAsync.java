package com.vegaasen.fun.radio.pinell.async.function.browse;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

import java.util.List;

/**
 * Fetches the content of a specific radioStation. The radioStation is actually defined as a Folder/Container,
 * so this method will help on fetching the contents of that candidate-container.
 * todo: ISSUES'R'US!
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 23.01.2016
 */
public class GetPreviousContainerContentAsync extends AbstractFragmentAsync<List<RadioStation>> {

    private static final String TAG = GetPreviousContainerContentAsync.class.getSimpleName();

    public GetPreviousContainerContentAsync(PinellService pinellService) {
        super(pinellService);
    }

    @Override
    @SafeVarargs
    protected final List<RadioStation> doInBackground(List<RadioStation>... lists) {
        Log.d(TAG, "Fetching previous container elements");
        return CollectionUtils.toList(pinellService.enterPreviousContainerAndListStations());
    }

    @Override
    protected void onPostExecute(List<RadioStation> aVoid) {
    }

    @Override
    protected void configureViewComponents() {
    }
}
