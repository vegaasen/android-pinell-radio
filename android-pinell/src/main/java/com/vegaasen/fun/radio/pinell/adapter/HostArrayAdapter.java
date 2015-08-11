package com.vegaasen.fun.radio.pinell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A simple deviceAdapter which holds the list of hosts available in the current network
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 0.1
 * @since 11.08.2015
 */
public class HostArrayAdapter extends ArrayAdapter<HostBean> {

    private static final String TAG = DeviceArrayAdapter.class.getSimpleName();
    private static final int MAX_HOSTNAME_LENGTH = 20, START = 0;

    private final Context context;
    private final WeakReference<AbstractActivity> activity;

    public HostArrayAdapter(Context context, int resource, int textResource, WeakReference<AbstractActivity> activity) {
        super(context, resource, textResource);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, String.format("Iterating through position {%s}", position));
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            final View deviceListView = layoutInflater.inflate(R.layout.listview_device, parent, false);
            final TextView radioName = (TextView) deviceListView.findViewById(R.id.listDeviceRadioName);
            final HostBean host = getHost(position);
            if (host != null) {
                final String hostName = host.getHostname();
                Log.d(TAG, String.format("Host is now being handled {%s}", hostName));
                radioName.setText(hostName.toCharArray(), START, hostName.length() > MAX_HOSTNAME_LENGTH ? MAX_HOSTNAME_LENGTH : hostName.length());
            }
            return deviceListView;
        }
        return super.getView(position, convertView, parent);
    }

    private HostBean getHost(int position) {
        if (activity == null) {
            return null;
        }
        final List<HostBean> hosts = activity.get().getHosts();
        if (hosts == null || hosts.isEmpty()) {
            return null;
        }
        return hosts.get(position);
    }

}
