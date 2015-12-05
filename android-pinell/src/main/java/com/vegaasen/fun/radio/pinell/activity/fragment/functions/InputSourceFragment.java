package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.common.collect.Lists;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.adapter.InputSourceAdapter;
import com.vegaasen.fun.radio.pinell.async.inputsource.InputSourceAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Fragment that handles everything that happens within the InputSource view fragment. This relates itself to changing inputSource etc.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 22.11.2015
 * @since 27.5.2015
 */
public class InputSourceFragment extends AbstractFragment {

    private static final String TAG = InputSourceFragment.class.getSimpleName();

    private View inputSourceView;
    private ListView listView;
    private List<RadioMode> inputSources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
//        if (!isWifiEnabledAndConnected()) {
//            inputSourceView = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//        } else
        if (!ApplicationContext.INSTANCE.isPinellDevice()) {
            inputSourceView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            inputSourceView = inflater.inflate(R.layout.fragment_input_sources, container, false);
            if (inputSourceView == null) {
                Log.e(TAG, "For some reason, the view were unable to be found. Dying");
                throw new RuntimeException("Missing required view in the initialization of the application");
            }
            configureComponents();
            refreshView();
        }
        return inputSourceView;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_settings_input_composite_white, R.string.sidebarSource);
    }

    public void refreshView() {
        if (inputSourceView == null) {
            Log.w(TAG, "Unable to list equalizers, as the equalizerView is not available");
            return;
        }
        new InputSourceAsync(getFragmentManager(), inputSourceView, listView, new WeakReference<>(this), getPinellService()).execute();
    }

    public void refreshDataSet(List<RadioMode> inputSources, RadioMode inputSource) {
        ApplicationContext.INSTANCE.setActiveRadioMode(inputSource);
        InputSourceAdapter equalizerAdapter = (InputSourceAdapter) listView.getAdapter();
        equalizerAdapter.updateRadioModes(inputSources);
        equalizerAdapter.updateCurrentRadioMode(inputSource);
        equalizerAdapter.notifyDataSetChanged();
        setInputSources(inputSources);
    }

    public void refreshDataSet(RadioMode inputSource) {
        ApplicationContext.INSTANCE.setActiveRadioMode(inputSource);
        InputSourceAdapter equalizerAdapter = (InputSourceAdapter) listView.getAdapter();
        equalizerAdapter.updateCurrentRadioMode(inputSource);
        equalizerAdapter.notifyDataSetChanged();
    }

    public List<RadioMode> getInputSources() {
        return inputSources;
    }

    public void setInputSources(List<RadioMode> inputSources) {
        this.inputSources = inputSources;
    }

    private void configureComponents() {
        listView = (ListView) inputSourceView.findViewById(R.id.inputSrcListOfSources);
        listView.setAdapter(new InputSourceAdapter(inputSourceView.getContext(), Lists.<RadioMode>newArrayList(), null));
    }

}
