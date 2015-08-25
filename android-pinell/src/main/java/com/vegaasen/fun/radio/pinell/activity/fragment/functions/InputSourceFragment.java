package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.adapter.InputSourceAdapter;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

import java.util.Collections;
import java.util.List;

/**
 * Fragment that handles everything that happens within the InputSource view fragment. This relates itself to changing inputSource etc.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 26.07.2015
 * @since 27.5.2015
 */
public class InputSourceFragment extends AbstractFragment {

    private static final String TAG = InputSourceFragment.class.getSimpleName();

    private View inputSourceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!getPinellService().isPinellDevice()) {
            inputSourceView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            inputSourceView = inflater.inflate(R.layout.fragment_input_sources, container, false);
            if (inputSourceView == null) {
                Log.e(TAG, "For some reason, the view were unable to be found. Dying");
                throw new RuntimeException("Missing required view in the initialization of the application");
            }
            changeActiveContent(container);
            listInputSourcesAvailable();
        }
        return inputSourceView;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_settings_input_composite_black, R.string.sidebarSource);
    }

    private void listInputSourcesAvailable() {
        if (inputSourceView == null) {
            Log.w(TAG, "Unable to list equalizers, as the inputSourcesView is not available");
            return;
        }
        final ListView inputSourceOverview = (ListView) inputSourceView.findViewById(R.id.inputSrcListOfSources);
        if (inputSourceOverview == null) {
            Log.w(TAG, "It seems like the inputSourceOverview is nilled, skipping");
            return;
        }
        final InputSourceAdapter inputSourcesAdapter = getInputSourceAdapter(inputSourceOverview);
        inputSourceOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Position {%s} and id {%s} clicked", position, id));
                final RadioMode selected = inputSourcesAdapter.getItem(position);
                getPinellService().setInputSource(selected);
                // Perform a refresh of the list of available inputSources
                listInputSourcesAvailable();
            }
        });
    }

    private InputSourceAdapter getInputSourceAdapter(ListView inputSourceOverview) {
        InputSourceAdapter inputSourceAdapter;
        final RadioMode currentInputSource = getCurrentInputSource();
        ApplicationContext.INSTANCE.setActiveRadioMode(currentInputSource);
        if (inputSourceOverview.getAdapter() == null) {
            inputSourceAdapter = new InputSourceAdapter(inputSourceView.getContext(), getInputSources(), currentInputSource);
            inputSourceOverview.setAdapter(inputSourceAdapter);
        } else {
            inputSourceAdapter = (InputSourceAdapter) inputSourceOverview.getAdapter();
            inputSourceAdapter.updateRadioModes(getInputSources());
            inputSourceAdapter.updateCurrentRadioMode(currentInputSource);
            inputSourceAdapter.notifyDataSetChanged();
        }
        return inputSourceAdapter;
    }

    public RadioMode getCurrentInputSource() {
        return getPinellService().getCurrentInputSource();
    }

    private List<RadioMode> getInputSources() {
        final List<RadioMode> inputSources = CollectionUtils.toList(getPinellService().listInputSources());
        Collections.sort(inputSources, new Comparators.InputSourceComparator());
        return inputSources;
    }

}
