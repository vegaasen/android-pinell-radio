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
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;

import java.util.List;

/**
 * Related to the browse stations activity / list radio stations
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 26.7.2015
 */
public class BrowseStationsActivity extends BaseAdapter {

    private static final String TAG = BrowseStationsActivity.class.getSimpleName();

    private final Context context;
    private final List<RadioStation> radioStations;

    private RadioStation currentRadioStation;

    public BrowseStationsActivity(Context context, List<RadioStation> radioStations) {
        this.context = context;
        this.radioStations = radioStations;
    }

    @SuppressWarnings("unused")
    public BrowseStationsActivity(Context context, List<RadioStation> radioStations, RadioStation currentRadioStation) {
        this.context = context;
        this.radioStations = radioStations;
        this.currentRadioStation = currentRadioStation;
    }

    @Override
    public int getCount() {
        return radioStations.size();
    }

    @Override
    public RadioStation getItem(int position) {
        return radioStations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            final RadioStation candidate = getItem(position);
            convertView = layoutInflater.inflate(R.layout.listview_selectables_advanced, parent, false);
            TextView radioStationCaption = (TextView) convertView.findViewById(R.id.selectableAdvancedItemTxt);
            radioStationCaption.setText(candidate.getName());
            configureCurrentRadioStation(convertView, candidate);
            return convertView;
        }
        Log.e(TAG, "How did we get here?");
        throw new IllegalStateException("This shouldn't happen :-)");
    }

    @SuppressWarnings("unused")
    public void updateCurrentRadioStation(RadioStation selected) {
        this.currentRadioStation = selected;
    }

    public void updateRadioStations(List<RadioStation> updatedRadioStations) {
        synchronized (radioStations) {
            radioStations.clear();
            radioStations.addAll(updatedRadioStations);
        }
    }

    private void configureCurrentRadioStation(View convertView, RadioStation candidate) {
        if (currentRadioStation != null && candidate.equals(currentRadioStation)) {
            RelativeLayout container = (RelativeLayout) convertView.findViewById(R.id.selectableAdvancedItem);
            container.setBackgroundColor(context.getResources().getColor(R.color.defaultElementSelectedColor));
        }
    }

}
