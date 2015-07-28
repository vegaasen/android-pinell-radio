package com.vegaasen.fun.radio.pinell.activity.abs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.service.PinellService;

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

    protected final Context context = this;

    public AbstractActivity() {
        super();
        Log.d(TAG, "AbstractActivity is setting the appropriate context");
        ApplicationContext.INSTANCE.setContext(context);
    }

    protected PinellService getPinellService() {
        return ApplicationContext.INSTANCE.getPinellService();
    }

    /**
     * Is the WiFi-services turned on, or are they still being set as disabled?
     *
     * @return state of WiFi
     */
    protected boolean isWifiEnabled() {
        return ApplicationContext.INSTANCE.getWifiManager().isWifiEnabled();
    }

}
