package com.vegaasen.fun.radio.pinell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.List;

/**
 * A simple deviceAdapter which holds the list of hosts available in the current network
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 0.1
 * @since 11.03.2015
 */
public class DeviceArrayAdapter extends ArrayAdapter<Host> {

    private static final String TAG = DeviceArrayAdapter.class.getSimpleName();
    private static final int MAX_HOSTNAME_LENGTH = 20, START = 0;

    private final Context context;
    private final List<Host> devices;

    public DeviceArrayAdapter(Context context, int resource, List<Host> objects) {
        super(context, resource, objects);
        this.context = context;
        this.devices = objects;
        Log.d(TAG, String.format("Detected {%s} hosts", objects.size()));
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        Log.d(TAG, String.format("Iterating through position {%s}", position));
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            final View deviceListView = layoutInflater.inflate(R.layout.listview_device, parent, false);
            final TextView radioName = (TextView) deviceListView.findViewById(R.id.listDeviceRadioName);
            final Host host = devices.get(position);
            if (host != null) {
                final String hostName = host.getHost();
                Log.d(TAG, String.format("Host is now being handled {%s}", hostName));
                radioName.setText(hostName.toCharArray(), START, hostName.length() > MAX_HOSTNAME_LENGTH ? MAX_HOSTNAME_LENGTH : hostName.length());
            }
            return deviceListView;
        }
        return super.getView(position, convertView, parent);
    }

    /**
     * Must be called whenever the adapter is updated due to some event.
     *
     * @param hosts _
     */
    public void updateDeviceList(List<Host> hosts) {
        devices.clear();
        if (hosts == null || hosts.isEmpty()) {
            Log.d(TAG, "Unable to update the deviceArrayAdapter due to the hostsList being empty. List was cleared, though.");
            return;
        }
        devices.addAll(hosts);
    }

}
