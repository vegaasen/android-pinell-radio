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
import com.vegaasen.fun.radio.pinell.adapter.EqualizerAdapter;
import com.vegaasen.fun.radio.pinell.async.equalizer.EqualizerAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * This fragment holds the information regarding the various equalizers available on the selected Pinell device
 * It handles the following action points:
 * - Lists all equalizers
 * - Select new equalizer
 * - Fetches the current selected equalizer
 * <p/>
 * It does not:
 * Support redefinition of existing "My Eq"
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 27.5.2015
 */
public class EqualizerFragment extends AbstractFragment {

    private static final String TAG = EqualizerFragment.class.getSimpleName();

    private View equalizerView;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
//        if (!isWifiEnabledAndConnected()) {
//            equalizerView = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//        } else
        if (!ApplicationContext.INSTANCE.isPinellDevice()) {
            equalizerView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            equalizerView = inflater.inflate(R.layout.fragment_equalizer, container, false);
            if (equalizerView == null) {
                Log.e(TAG, "For some reason, the view were unable to be found. Dying");
                throw new RuntimeException("Missing required view in the initialization of the application");
            }
            configureComponents();
            refreshView();
        }
        return equalizerView;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_equalizer_white, R.string.sidebarEqualizer);
    }

    public void refreshView() {
        if (equalizerView == null) {
            Log.w(TAG, "Unable to list equalizers, as the equalizerView is not available");
            return;
        }
        new EqualizerAsync(getFragmentManager(), equalizerView, listView, new WeakReference<>(this), getPinellService()).execute();
    }

    public void refreshDataSet(List<Equalizer> equalizers, Equalizer currentEqualizer) {
        ApplicationContext.INSTANCE.setActiveEqualizer(currentEqualizer);
        EqualizerAdapter equalizerAdapter = (EqualizerAdapter) listView.getAdapter();
        equalizerAdapter.updateEqualizers(equalizers);
        equalizerAdapter.updateCurrentEqualizer(currentEqualizer);
        equalizerAdapter.notifyDataSetChanged();
    }

    private void configureComponents() {
        listView = (ListView) equalizerView.findViewById(R.id.equalizerListOfEqualizers);
        listView.setAdapter(new EqualizerAdapter(equalizerView.getContext(), Lists.<Equalizer>newArrayList(), null));
    }

}
