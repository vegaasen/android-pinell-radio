package com.vegaasen.fun.radio.pinell.activity.abs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
        ApplicationContext.INSTANCE.setContext(context);
    }

    protected PinellService getPinellService() {
        return ApplicationContext.INSTANCE.getPinellService();
    }

}
