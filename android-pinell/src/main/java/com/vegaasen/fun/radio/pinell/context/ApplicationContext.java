package com.vegaasen.fun.radio.pinell.context;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.service.impl.PinellServiceImpl;
import com.vegaasen.fun.radio.pinell.util.NetworkUtils;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public enum ApplicationContext {

    INSTANCE;

    private static final String TAG = ApplicationContext.class.getSimpleName();

    private Context context;
    private PinellService pinellService;

    public PinellService getPinellService() {
        if (pinellService == null) {
            pinellService = new PinellServiceImpl();
            final String subnet = NetworkUtils.fromIntToIp(getWifiManager().getConnectionInfo().getIpAddress());
            Log.d(TAG, String.format("Device connected to subnet {%s}", subnet));
            pinellService.setCurrentSubnet(subnet);
        }
        return pinellService;
    }

    /**
     * Required: This must be set prior to everything. Currently, this is set in the AbstractActivity class, which is used in the MainActivity :-).
     *
     * @param context _
     */
    public void setContext(Context context) {
        this.context = context;
    }

    protected WifiManager getWifiManager() {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

}
