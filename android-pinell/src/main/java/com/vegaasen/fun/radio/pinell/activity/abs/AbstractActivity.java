package com.vegaasen.fun.radio.pinell.activity.abs;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.service.impl.PinellServiceImpl;
import com.vegaasen.fun.radio.pinell.util.NetworkUtils;

/**
 * Simple layer abstraction for all common screens in the application.
 * It works as the "unibody" design
 * <p/>
 * Todo: move the initialization from the AbstractActivity to a context-wide location instead
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public abstract class AbstractActivity extends FragmentActivity {

    private static final String TAG = AbstractActivity.class.getSimpleName();

    private PinellService pinellService;

    protected final Context context = this;

    protected PinellService getPinellService() {
        if (pinellService == null) {
            pinellService = new PinellServiceImpl();
            final String subnet = NetworkUtils.fromIntToIp(getWifiManager().getConnectionInfo().getIpAddress());
            Log.d(TAG, String.format("Device connected to subnet {%s}", subnet));
            pinellService.setCurrentSubnet(subnet);
        }
        return pinellService;
    }

    protected WifiManager getWifiManager() {
        return (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

}
