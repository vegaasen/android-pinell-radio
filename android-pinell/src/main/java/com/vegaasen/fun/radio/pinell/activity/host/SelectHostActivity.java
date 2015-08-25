package com.vegaasen.fun.radio.pinell.activity.host;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.adapter.HostArrayAdapter;
import com.vegaasen.fun.radio.pinell.discovery.abs.AbstractHostDiscovery;
import com.vegaasen.fun.radio.pinell.discovery.mode.XANHostDiscovery;
import com.vegaasen.fun.radio.pinell.discovery.model.NetInfo;
import com.vegaasen.fun.radio.pinell.listeners.DeviceListListener;

import java.lang.ref.WeakReference;

import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_CIDR;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_CIDR_CUSTOM;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_IP_CUSTOM;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_IP_END;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_IP_START;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_CIDR;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_CIDR_CUSTOM;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_IP_CUSTOM;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_IP_END;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_IP_START;

/**
 * This represents the selectable host activity. Its main objective is just to let the user select which host that he/she would like to connect to.
 * Todo: This activity must fetch its hosts from a AsyncActivity instead of using the TelnetUtils :-/ Too bad!
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 0.1
 * @see DeviceListListener
 * @since 29.03.2015
 */
public class SelectHostActivity extends AbstractActivity {

    private static final String TAG = SelectHostActivity.class.getSimpleName();

    private int currentNetwork = 0;
    private long networkIp = 0;
    private long networkStart = 0;
    private long networkEnd = 0;
    private AbstractHostDiscovery hostDiscovery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableTaskbarSpinner();
        discoveryOnCreate(savedInstanceState);
        setContentView(R.layout.dialog_device_chooser);
        configureActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        discoveryOnResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        deregisterReceiver();
        cancel();
    }

    @Override
    protected void configureNetworkInformation() {
        if (currentNetwork != net.hashCode()) {
            Log.d(TAG, "Network info has changed");
            currentNetwork = net.hashCode();
        } else {
            return;
        }
        // Get ip information
        networkIp = NetInfo.getUnsignedLongFromIp(net.ip);
        if (prefs.getBoolean(KEY_IP_CUSTOM, DEFAULT_IP_CUSTOM)) {
            // Custom IP
            networkStart = NetInfo.getUnsignedLongFromIp(prefs.getString(KEY_IP_START, DEFAULT_IP_START));
            networkEnd = NetInfo.getUnsignedLongFromIp(prefs.getString(KEY_IP_END, DEFAULT_IP_END));
        } else {
            // Custom CIDR
            if (prefs.getBoolean(KEY_CIDR_CUSTOM, DEFAULT_CIDR_CUSTOM)) {
                net.cidr = Integer.parseInt(prefs.getString(KEY_CIDR, DEFAULT_CIDR));
            }
            // Detected IP
            int shift = (32 - net.cidr);
            if (net.cidr < 31) {
                networkStart = (networkIp >> shift << shift) + 1;
                networkEnd = (networkStart | ((1 << shift) - 1)) - 1;
            } else {
                networkStart = (networkIp >> shift << shift);
                networkEnd = (networkStart | ((1 << shift) - 1));
            }
            // Reset ip start-end (is it really convenient ?)
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(KEY_IP_START, NetInfo.getIpFromLongUnsigned(networkStart));
            edit.putString(KEY_IP_END, NetInfo.getIpFromLongUnsigned(networkEnd));
            edit.apply();
        }
    }

    @Override
    public void cancel() {
        Log.i(TAG, "Stopping the discovery of items");
        if (hostDiscovery != null) {
            hostDiscovery.cancel(true);
            hostDiscovery.hardCancel();
            hostDiscovery = null;
        }
    }

    private void configureActions() {
        final ImageButton refreshDevices = (ImageButton) findViewById(R.id.btnDialogRefreshDevices);
        final AbstractActivity currentActivity = this;
        refreshDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, R.string.devices_refreshing, Toast.LENGTH_SHORT).show();
                refreshHostsToList(currentActivity);
            }
        });
    }

    private void refreshHostsToList(AbstractActivity activity) {
        final ListView deviceOverview = (ListView) findViewById(R.id.listDevices);
        if (deviceOverview != null) {
            if (deviceOverview.getAdapter() == null) {
                adapter = new HostArrayAdapter(context, R.layout.listview_device, R.id.listDeviceRadioName, new WeakReference<AbstractActivity>(this));
                deviceOverview.setAdapter(adapter);
                deviceOverview.setItemsCanFocus(false);
                deviceOverview.setOnItemClickListener(new DeviceListListener(new WeakReference<>(activity), getPinellService()));
//                deviceOverview.setEmptyView();
            } else {
                Log.d(TAG, "Updating existing adapter");
                adapter.clear();
                clearHosts();
                cancel();
            }
        }
        hostDiscovery = new XANHostDiscovery(activity);
        hostDiscovery.setNetwork(networkIp, networkStart, networkEnd);
        hostDiscovery.execute();
        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);
    }

}
