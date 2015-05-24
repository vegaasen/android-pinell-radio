package com.vegaasen.fun.radio.pinell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.vegaasen.fun.radio.pinell.R;

import java.util.Map;

/**
 * This part is supposed to auto populate the device information - that is, the information regarding the selected device
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class DeviceInformationAdapter extends BaseAdapter {

    private static final String TAG = DeviceInformationAdapter.class.getSimpleName();
    public static final String NOT_FOUND = "";

    private final Context context;
    private Map<String, String> information;
    private String[] informationKeys;

    public DeviceInformationAdapter(Map<String, String> information, Context ctx) {
        context = ctx;
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
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            convertView = layoutInflater.inflate(R.layout.listview_information, parent, false);
            final String referenceKey = getInformationKey(informationKeys[position]);
            final String referenceValue = String.valueOf(getItem(position));
            if (!Strings.isNullOrEmpty(referenceKey) && !Strings.isNullOrEmpty(referenceValue)) {
                final TextView key = (TextView) convertView.findViewById(R.id.lblDeviceInformation);
                final TextView value = (TextView) convertView.findViewById(R.id.txtDeviceInformation);
                key.setText(referenceKey);
                value.setText(referenceValue);
            }
            Log.d(TAG, String.format("Information for {%s, %s}", referenceKey, referenceValue));
            //todo: the rest
            return convertView;
        }
        Log.e(TAG, "How did we get here?");
        //todo improve upon this code
        throw new IllegalStateException("");
    }

    public void updateDeviceInformation(Map<String, String> information) {
        this.information = information;
        informationKeys = this.information.keySet().toArray(new String[getCount()]);
    }

    private String getInformationKey(String informationKey) {
        return informationKey;
    }
}
