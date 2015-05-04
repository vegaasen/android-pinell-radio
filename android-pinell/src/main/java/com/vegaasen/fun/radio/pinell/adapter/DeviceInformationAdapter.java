package com.vegaasen.fun.radio.pinell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.google.common.base.Strings;

import java.util.Map;

/**
 * todo: unfinished.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class DeviceInformationAdapter extends BaseAdapter {

    private static final String TAG = DeviceInformationAdapter.class.getSimpleName();
    public static final String NOT_FOUND = "";

    private Map<String, String> information;
    private String[] informationKeys;

    public DeviceInformationAdapter(Map<String, String> information) {
        updateDeviceInformation(information);
    }

    @Override
    public int getCount() {
        return information.size();
    }

    @Override
    public String getItem(int position) {
        Log.d(TAG, String.format("Fetching item from position {%s}", position));
        if (position <= informationKeys.length) {
            return information.get(informationKeys[position]);
        }
        Log.w(TAG, String.format("Position {%s} not valid", position));
        return NOT_FOUND;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String referenceKey = getInformationKey(informationKeys[position]);
        final String referenceValue = String.valueOf(getItem(position));
        if(!Strings.isNullOrEmpty(referenceKey) && !Strings.isNullOrEmpty(referenceValue)) {

        }
        Log.d(TAG, String.format("Information for {%s, %s}", referenceKey, referenceValue));
        //todo: the rest
        return convertView;
    }

    public void updateDeviceInformation(Map<String, String> information) {
        this.information = information;
        informationKeys = this.information.keySet().toArray(new String[getCount()]);
    }

    private String getInformationKey(String informationKey) {
        return informationKey;
    }
}
