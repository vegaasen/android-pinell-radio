package com.vegaasen.fun.radio.pinell.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

import java.util.List;

/**
 * Simple an adapter for the various InputSources defined on the Pinell terminal.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @see EqualizerAdapter
 */
public class InputSourceAdapter extends BaseAdapter {

    private static final String TAG = InputSourceAdapter.class.getSimpleName();

    private final Context context;
    private final List<RadioMode> radioModes;

    private RadioMode currentRadioMode;

    public InputSourceAdapter(Context context, List<RadioMode> radioModes, RadioMode currentRadioMode) {
        this.context = context;
        this.radioModes = radioModes;
        this.currentRadioMode = currentRadioMode;
    }

    @Override
    public int getCount() {
        return radioModes.size();
    }

    @Override
    public RadioMode getItem(int position) {
        return radioModes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            final RadioMode candidateRadio = getItem(position);
            convertView = layoutInflater.inflate(R.layout.listview_selectables_advanced, parent, false);
            TextView caption = (TextView) convertView.findViewById(R.id.selectableAdvancedItemTxt);
            ImageView image = (ImageView) convertView.findViewById(R.id.selectableAdvancedItemImg);
            image.setImageResource(R.drawable.ic_settings_input_composite_white);
            caption.setText(candidateRadio.getName());
            if (candidateRadio.equals(currentRadioMode)) {
                final Resources resources = context.getResources();
                RelativeLayout equalizerContainer = (RelativeLayout) convertView.findViewById(R.id.selectableAdvancedItem);
                equalizerContainer.setBackgroundColor(resources.getColor(R.color.newSidebarBackgroundColor));
                caption.setTextColor(resources.getColor(R.color.newTitleTextColor));
                image.setAlpha(1.0f);
            }
            if (!candidateRadio.isSelectable()) {
                convertView.setEnabled(false);
            }
            return convertView;
        }
        Log.e(TAG, "How did we get here?");
        throw new IllegalStateException("This shouldn't happen :-)");
    }

    public void updateCurrentRadioMode(RadioMode selected) {
        this.currentRadioMode = selected;
    }

    public void updateRadioModes(List<RadioMode> updatedEqualizers) {
        synchronized (radioModes) {
            radioModes.clear();
            radioModes.addAll(updatedEqualizers);
        }
    }

}