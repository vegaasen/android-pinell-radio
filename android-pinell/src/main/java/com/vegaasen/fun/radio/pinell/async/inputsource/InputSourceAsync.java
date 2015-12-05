package com.vegaasen.fun.radio.pinell.async.inputsource;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.InputSourceFragment;
import com.vegaasen.fun.radio.pinell.adapter.InputSourceAdapter;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Represents all the normal procedure details related to the input-source fragment that requires async stuff
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @version 22.11.2015
 * @since 22.11.2015
 */
public class InputSourceAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = InputSourceAsync.class.getSimpleName();

    private final View view;
    private final WeakReference<InputSourceFragment> inputSourceFragment;
    private final ListView inputSourceList;

    private ProgressBar spinner;
    private List<RadioMode> radioModes;
    private RadioMode currentMode;

    public InputSourceAsync(FragmentManager fragmentManager, View view, ListView equalizerOverview, WeakReference<InputSourceFragment> inputSourceFragment, PinellService pinellService) {
        super(fragmentManager, null, pinellService, null);
        this.inputSourceFragment = inputSourceFragment;
        this.view = view;
        this.inputSourceList = equalizerOverview;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        configureViewComponents();
        if (view == null) {
            Log.w(TAG, "It seems like the view is nilled, skipping");
            return null;
        }
        currentMode = pinellService.getCurrentInputSource();
        List<RadioMode> inputSources = inputSourceFragment.get().getInputSources();
        radioModes = CollectionUtils.isEmpty(inputSources) ? getInputSources() : inputSources;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        final InputSourceFragment inputSourceFragment = this.inputSourceFragment.get();
        if (!inputSourceFragment.isAdded()) {
            Log.d(TAG, "Fragment removed. Skipping handling");
            return;
        }
        inputSourceFragment.refreshDataSet(radioModes, currentMode);
        final InputSourceAdapter inputSourceAdapter = (InputSourceAdapter) inputSourceList.getAdapter();
        inputSourceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Position {%s} and id {%s} clicked", position, id));
                final RadioMode selectedMode = inputSourceAdapter.getItem(position);
                new InputSourceSelectModeAsync(pinellService, selectedMode).execute();
                inputSourceFragment.refreshDataSet(selectedMode);
            }
        });
        if (spinner != null) {
            spinner.setVisibility(View.GONE);
        }

    }

    @Override
    protected void configureViewComponents() {
        spinner = (ProgressBar) view.findViewById(R.id.inputSourceSpinner);
    }

    private List<RadioMode> getInputSources() {
        final List<RadioMode> inputSources = CollectionUtils.toList(pinellService.listInputSources());
        Collections.sort(inputSources, new Comparators.InputSourceComparator());
        return inputSources;
    }
}
