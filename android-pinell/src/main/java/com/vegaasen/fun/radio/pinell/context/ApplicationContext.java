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
 * Represents the context of the application itself. It also contains a few convenience methods like "isPinellDevice" etc.
 * The determination of the Pinell-device is handled in the referenced class
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 22.11.2015
 * @see com.vegaasen.fun.radio.pinell.async.function.SetPinellHostAsync
 * @since 29.3.2015
 */
public enum ApplicationContext {

    INSTANCE;

    private static final String TAG = ApplicationContext.class.getSimpleName();

    private Context context;
    private PinellService pinellService;
    private RadioStation activeRadioStation;
    private Equalizer activeEqualizer;
    private PinellRadioMode activeRadioMode;
    private boolean radioConnected = false, pinellDevice = false;

    public PinellService getPinellService() {
        if (pinellService == null) {
            final String subnet = NetworkUtils.fromIntToIp(getWifiManager().getConnectionInfo().getIpAddress());
//            if (Strings.isNullOrEmpty(subnet)) {
//                return null;
//            }
            pinellService = new PinellServiceImpl();
            Log.d(TAG, String.format("Device connected to subnet {%s}", subnet));
            pinellService.setCurrentSubnet("192.168.0");
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

    public boolean isRadioConnected() {
        return radioConnected;
    }

    public void setRadioConnected(boolean radioConnected) {
        this.radioConnected = radioConnected;
    }

    public boolean isPinellDevice() {
        return pinellDevice;
    }

    public void setPinellDevice(boolean pinellDevice) {
        this.pinellDevice = pinellDevice;
    }
}
