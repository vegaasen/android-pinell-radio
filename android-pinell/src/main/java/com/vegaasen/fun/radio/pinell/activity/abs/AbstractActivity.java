package com.vegaasen.fun.radio.pinell.activity.abs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.discovery.model.NetInfo;
import com.vegaasen.fun.radio.pinell.service.PinellService;

import java.util.ArrayList;
import java.util.List;

import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_INTF;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_MOBILE;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_INTF;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_MOBILE;

/**
 * Simple layer abstraction for all common screens in the application.
 * It works as the "unibody" design
 * <p/>
 * Todo: move the initialization from the AbstractActivity to a context-wide location instead
 * Todo: refactor
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public abstract class AbstractActivity extends FragmentActivity {

    private static final String TAG = AbstractActivity.class.getSimpleName();
    private static final String EMPTY = "";

    public static final long VIBRATE = (long) 250;

    private List<HostBean> hosts = new ArrayList<>();
    private ConnectivityManager connMgr;

    public SharedPreferences prefs = null;
    public NetInfo net = null;

    protected final Context context = this;

    protected ArrayAdapter<?> adapter;
    protected String info_ip_str = EMPTY;
    protected String info_in_str = EMPTY;
    protected String info_mo_str = EMPTY;

    public AbstractActivity() {
        super();
        Log.d(TAG, "AbstractActivity is setting the appropriate context");
        ApplicationContext.INSTANCE.setContext(context);
    }

    /**
     * These settings may be overridden by the implementing classes
     */
    public void cancel() {
    }

    public void addHost(HostBean host) {
        host.setPosition(hosts.size());
        hosts.add(host);
        adapter.add(null);
    }

    public List<HostBean> getHosts() {
        return hosts;
    }

    protected void clearHosts() {
        hosts.clear();
    }

    protected void enableTaskbarSpinner() {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    /**
     * These settings may be overridden by the implementing classes
     */
    protected void configureNetworkInformation() {
    }

    protected void discoveryOnCreate(Bundle savedInstanceState) {
//        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        net = new NetInfo(context);
    }

    protected void discoveryOnResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    protected void deregisterReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    protected PinellService getPinellService() {
        return ApplicationContext.INSTANCE.getPinellService();
    }

    /**
     * Is the WiFi-services turned on, or are they still being set as disabled?
     *
     * @return state of WiFi
     */
    protected boolean isWifiEnabledAndConnected() {
        if (!ApplicationContext.INSTANCE.getWifiManager().isWifiEnabled()) {
            Log.d(TAG, "Wifi is not enabled");
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Log.d(TAG, "The connectionManager is nilled");
            return false;
        }
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && wifiInfo.isConnected();
    }

    /**
     * Is the current device connected to something? Does not imply that the device is a Pinell
     *
     * @return _
     */
    protected boolean isConnectedToSomeDevice() {
        return getPinellService() != null && getPinellService().getSelectedHost() != null;
    }

    public void makeToast(int messageReferenceId) {
        Toast.makeText(getApplicationContext(), messageReferenceId, Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            info_ip_str = EMPTY;
            info_mo_str = EMPTY;
            // Wifi state
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int WifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    //Log.d(TAG, "WifiState=" + WifiState);
                    switch (WifiState) {
                        case WifiManager.WIFI_STATE_ENABLING:
                            info_in_str = getString(R.string.genericUnknown);
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            info_in_str = getString(R.string.genericUnknown);
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            info_in_str = getString(R.string.genericUnknown);
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            info_in_str = getString(R.string.genericUnknown);
                            break;
                        default:
                            info_in_str = getString(R.string.genericUnknown);
                    }
                }

                if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) && net.getWifiInfo()) {
                    SupplicantState sstate = net.getSupplicantState();
                    if (sstate == SupplicantState.SCANNING) {
                        info_in_str = getString(R.string.genericUnknown);
                    } else if (sstate == SupplicantState.ASSOCIATING) {
                        info_in_str = getString(R.string.genericUnknown,
                                (net.ssid != null ? net.ssid : (net.bssid != null ? net.bssid
                                        : net.macAddress)));
                    } else if (sstate == SupplicantState.COMPLETED) {
                        info_in_str = getString(R.string.genericUnknown, net.ssid);
                    }
                }
            }

            // 3G(connected) -> Wifi(connected)
            // Support Ethernet, with ConnectivityManager.TYPE_ETHER=3
            final NetworkInfo ni = connMgr.getActiveNetworkInfo();
            if (ni != null) {
                //Log.i(TAG, "NetworkState="+ni.getDetailedState());
                if (ni.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    int type = ni.getType();
                    //Log.i(TAG, "NetworkType="+type);
                    if (type == ConnectivityManager.TYPE_WIFI) { // WIFI
                        net.getWifiInfo();
                        if (net.ssid != null) {
                            net.getIp();
                            info_ip_str = getString(R.string.genericUnknown, net.ip, net.cidr, net.intf);
                            info_in_str = getString(R.string.genericUnknown, net.ssid);
                            info_mo_str = getString(R.string.genericUnknown, getString(
                                    R.string.genericUnknown, net.speed, WifiInfo.LINK_SPEED_UNITS));
                        }
                    } else if (type == ConnectivityManager.TYPE_MOBILE) { // 3G
                        if (prefs.getBoolean(KEY_MOBILE, DEFAULT_MOBILE)
                                || prefs.getString(KEY_INTF, DEFAULT_INTF) != null) {
                            net.getMobileInfo();
                            if (net.carrier != null) {
                                net.getIp();
                                info_ip_str = getString(R.string.genericUnknown, net.ip, net.cidr, net.intf);
                                info_in_str = getString(R.string.genericUnknown, net.carrier);
                                info_mo_str = getString(R.string.genericUnknown,
                                        getString(R.string.genericUnknown));
                            }
                        }
                    } else if (type == 3 || type == 9) { // ETH
                        net.getIp();
                        info_ip_str = getString(R.string.genericUnknown, net.ip, net.cidr, net.intf);
                        info_in_str = EMPTY;
                        info_mo_str = getString(R.string.genericUnknown) + getString(R.string.genericUnknown);
                        Log.i(TAG, "Ethernet connectivity detected!");
                    } else {
                        Log.i(TAG, "Connectivity unknown!");
                        info_mo_str = getString(R.string.genericUnknown)
                                + getString(R.string.genericUnknown);
                    }
                }
            }
            configureNetworkInformation();
        }
    };

}
