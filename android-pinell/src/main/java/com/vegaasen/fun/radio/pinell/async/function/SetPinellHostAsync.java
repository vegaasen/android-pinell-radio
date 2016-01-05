package com.vegaasen.fun.radio.pinell.async.function;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

/**
 * Host selection and device determination (is it a Pinell device at all?)
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @see com.vegaasen.fun.radio.pinell.listeners.DeviceListListener
 * @see ApplicationContext
 */
public class SetPinellHostAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = SetPinellHostAsync.class.getSimpleName();

    private final Host host;

    public SetPinellHostAsync(PinellService pinellService, Host host) {
        super(pinellService);
        this.host = host;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final boolean pinellHostSet = pinellService.setCurrentPinellHost(host);
        boolean pinellDevice = pinellService.isPinellDevice();
        Log.d(TAG, String.format("Setting {%s} as the host were successful {%s}. It seems like the device is a pinellDevice {%s}", host.toString(), pinellHostSet, pinellDevice));
        ApplicationContext.INSTANCE.setPinellDevice(pinellDevice);
        if (pinellDevice) {
            ApplicationContext.INSTANCE.getStorageService().store(host);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    @Override
    protected void configureViewComponents() {

    }
}
