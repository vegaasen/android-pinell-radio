package com.vegaasen.fun.radio.pinell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Map;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class DeviceInformationAdapter extends BaseAdapter {

    private static final String TAG = DeviceInformationAdapter.class.getSimpleName();

    private final Map<String, String> information;
    private final String[] informationKeys;

    public DeviceInformationAdapter(Map<String, String> information) {
        this.information = information;
        informationKeys = this.information.keySet().toArray(new String[getCount()]);
    }

    @Override
    public int getCount() {
        return information.size();
    }

    @Override
    public Object getItem(int position) {
        return information.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String referenceKey = getInformationKey(informationKeys[position]);
        final String referenceValue = String.valueOf(getItem(position));
        Log.d(TAG, String.format("Information for {%s, %s}", referenceKey, referenceValue));
        //todo: the rest
        return convertView;
    }

    private String getInformationKey(String informationKey) {
        return informationKey;
    }
}
