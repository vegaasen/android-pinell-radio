package com.vegaasen.fun.radio.pinell.async.function;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * Usage:
 * boolean deviceOn = false;
 * try {
 * deviceOn = new IsDeviceOnAsync(getPinellService()).execute().get(2, TimeUnit.SECONDS);
 * } catch (Exception e) {
 * Log.d(TAG, "Unable to fetch deviceOn status");
 * }
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class IsDeviceOnAsync extends AbstractFragmentAsync<Boolean> {

    private static final String TAG = IsDeviceOnAsync.class.getSimpleName();

    public IsDeviceOnAsync(PinellService pinellService) {
        super(pinellService);
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {

    }

    @Override
    protected void configureViewComponents() {

    }

    @Override
    protected Boolean doInBackground(Boolean... voids) {
        Log.d(TAG, "Discovering powered on");
        return pinellService.isPoweredOn();
    }

}
