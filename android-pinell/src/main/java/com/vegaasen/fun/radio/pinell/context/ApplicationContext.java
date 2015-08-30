package com.vegaasen.fun.radio.pinell.context;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.model.PinellRadioMode;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.service.impl.PinellServiceImpl;
import com.vegaasen.fun.radio.pinell.util.NetworkUtils;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public enum ApplicationContext {

    INSTANCE;

    private static final String TAG = ApplicationContext.class.getSimpleName();

    private Context context;
    private PinellService pinellService;
    private RadioStation activeRadioStation;
    private Equalizer activeEqualizer;
    private PinellRadioMode activeRadioMode;

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

    public WifiManager getWifiManager() {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public RadioStation getActiveRadioStation() {
        return activeRadioStation;
    }

    public void setActiveRadioStation(RadioStation activeRadioStation) {
        this.activeRadioStation = activeRadioStation;
    }

    public Equalizer getActiveEqualizer() {
        return activeEqualizer;
    }

    public void setActiveEqualizer(Equalizer activeEqualizer) {
        this.activeEqualizer = activeEqualizer;
    }

    public PinellRadioMode getActiveRadioMode() {
        return activeRadioMode;
    }

    public void setActiveRadioMode(RadioMode activeRadioMode) {
        if (activeRadioMode == null) {
            return;
        }
        Log.d(TAG, String.format("RadioMode {%s} selected", activeRadioMode.toString()));
        this.activeRadioMode = PinellRadioMode.fromName(activeRadioMode.getName());
    }
}
