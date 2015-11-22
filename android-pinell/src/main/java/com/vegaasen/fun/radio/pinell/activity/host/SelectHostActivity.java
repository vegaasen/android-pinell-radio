package com.vegaasen.fun.radio.pinell.activity.host;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.adapter.HostArrayAdapter;
import com.vegaasen.fun.radio.pinell.discovery.abs.AbstractHostDiscovery;
import com.vegaasen.fun.radio.pinell.discovery.mode.XANHostDiscovery;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.discovery.model.NetInfo;
import com.vegaasen.fun.radio.pinell.listeners.DeviceListListener;

import java.lang.ref.WeakReference;
import java.util.Collections;

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
 * Todo: Add some functionality to (in the background) fetch active: RadioStation, RadioMode and Equalizer
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 0.2
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
    private TextView refreshHelpText, refreshInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableTaskbarSpinner();
        discoveryOnCreate();
        setContentView(R.layout.dialog_device_chooser);
        configureElements();
        configureActions();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        discoveryOnResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        deregisterReceiver();
        cancel();
    }

    @Override
    public void cancel() {
        Log.i(TAG, "Stopping the discovery of items");
        if (hostDiscovery != null) {
            hostDiscovery.cancel(true);
            hostDiscovery.hardCancel();
            hostDiscovery = null;
            setProgressBarIndeterminateVisibility(false);
        }
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

    private void configureElements() {
        refreshHelpText = (TextView) findViewById(R.id.txtListClickRefresh);
        refreshInProgress = (TextView) findViewById(R.id.txtListClickRefreshInProgress);
    }

    private void configureActions() {
        final AbstractActivity currentActivity = this;
        refreshHelpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            } else {
                adapter.clear();
                clearHosts();
                cancel();
            }
            deviceOverview.setEmptyView(configureTextViews());
        }
        hostDiscovery = new XANHostDiscovery(activity);
        hostDiscovery.setNetwork(networkIp, networkStart, networkEnd);
        hostDiscovery.execute();
        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);
        Log.e(TAG, "#### Remove the hardcoded host!!! ####");
        HostBean host = new HostBean();
        host.setIpAddress("192.168.0.102");
        host.setHostname(host.getIpAddress());
        host.setPortsOpen(Collections.singleton(2244));
        addHost(host);
    }

    private TextView configureTextViews() {
        refreshHelpText.setVisibility(View.GONE);
        refreshInProgress.setVisibility(View.VISIBLE);
        return refreshInProgress;
    }

    @Override
    public void postLoadingActions() {
        refreshHelpText.setVisibility(View.VISIBLE);
        refreshInProgress.setVisibility(View.GONE);
    }

}
