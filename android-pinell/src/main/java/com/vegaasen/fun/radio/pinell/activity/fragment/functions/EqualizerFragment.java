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
import com.vegaasen.fun.radio.pinell.adapter.EqualizerAdapter;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.util.CollectionUtils;
import com.vegaasen.fun.radio.pinell.util.Comparators;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;

import java.util.Collections;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!getPinellService().isPinellDevice()) {
            equalizerView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            equalizerView = inflater.inflate(R.layout.fragment_equalizer, container, false);
            if (equalizerView == null) {
                Log.e(TAG, "For some reason, the view were unable to be found. Dying");
                throw new RuntimeException("Missing required view in the initialization of the application");
            }
            changeActiveContent(container);
            listEqualizersAvailable();
        }
        return equalizerView;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_equalizer_black, R.string.sidebarEqualizer);
    }

    private void listEqualizersAvailable() {
        if (equalizerView == null) {
            Log.w(TAG, "Unable to list equalizers, as the equalizerView is not available");
            return;
        }
        final ListView equalizerOverview = (ListView) equalizerView.findViewById(R.id.equalizerListOfEqualizers);
        if (equalizerOverview == null) {
            Log.w(TAG, "It seems like the equalizerOverview is nilled, skipping");
            return;
        }
        final EqualizerAdapter equalizerAdapter = getEqualizerOverview(equalizerOverview);
        equalizerOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("Position {%s} and id {%s} clicked", position, id));
                final Equalizer selectedEqualizer = equalizerAdapter.getItem(position);
                getPinellService().setEqualizer(selectedEqualizer);
                // Perform a refresh of the list of available equalizers
                listEqualizersAvailable();
            }
        });
    }

    private EqualizerAdapter getEqualizerOverview(ListView equalizerOverview) {
        EqualizerAdapter equalizerAdapter;
        final Equalizer currentEqualizer = getCurrentEqualizer();
        final List<Equalizer> equalizers = getEqualizers();
        ApplicationContext.INSTANCE.setActiveEqualizer(currentEqualizer);
        if (equalizerOverview.getAdapter() == null) {
            equalizerAdapter = new EqualizerAdapter(equalizerView.getContext(), equalizers, currentEqualizer);
            equalizerOverview.setAdapter(equalizerAdapter);
        } else {
            equalizerAdapter = (EqualizerAdapter) equalizerOverview.getAdapter();
            equalizerAdapter.updateEqualizers(equalizers);
            equalizerAdapter.updateCurrentEqualizer(currentEqualizer);
            equalizerAdapter.notifyDataSetChanged();
        }
        return equalizerAdapter;
    }

    public Equalizer getCurrentEqualizer() {
        return getPinellService().getCurrentEqualizer();
    }

    private List<Equalizer> getEqualizers() {
        final List<Equalizer> equalizers = CollectionUtils.toList(getPinellService().listEqualizers());
        Collections.sort(equalizers, new Comparators.EqualizerComparator());
        return equalizers;
    }

}
