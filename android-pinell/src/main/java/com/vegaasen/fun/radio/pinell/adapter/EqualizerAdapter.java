package com.vegaasen.fun.radio.pinell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;

import java.util.List;

/**
 * Adapter related to the equalizer tasks
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 12.07.2015
 */
public class EqualizerAdapter extends BaseAdapter {

    private static final String TAG = EqualizerAdapter.class.getSimpleName();

    private final Context context;
    private final List<Equalizer> equalizers;
    
    private Equalizer currentEqualizer;

    public EqualizerAdapter(Context context, List<Equalizer> equalizers, Equalizer currentEqualizer) {
        this.context = context;
        this.equalizers = equalizers;
        this.currentEqualizer = currentEqualizer;
    }

    @Override
    public int getCount() {
        return equalizers.size();
    }

    @Override
    public Equalizer getItem(int position) {
        return equalizers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            final Equalizer candidateEqualizer = getItem(position);
            convertView = layoutInflater.inflate(R.layout.listview_selectables, parent, false);
            TextView equalizerCaption = (TextView) convertView.findViewById(R.id.selectableItemTxt);
            equalizerCaption.setText(candidateEqualizer.getName());
            if (candidateEqualizer.equals(currentEqualizer)) {
                RelativeLayout equalizerContainer = (RelativeLayout) convertView.findViewById(R.id.selectableItem);
                equalizerContainer.setBackgroundColor(context.getResources().getColor(R.color.defaultElementSelectedColor));
            }
            return convertView;
        }
        Log.e(TAG, "How did we get here?");
        throw new IllegalStateException("This shouldn't happen :-)");
    }

    public void updateCurrentEqualizer(Equalizer selected) {
        this.currentEqualizer = selected;
    }

    public void updateEqualizers(List<Equalizer> updatedEqualizers) {
        synchronized (equalizers) {
            equalizers.clear();
            equalizers.addAll(updatedEqualizers);
        }
    }

}
