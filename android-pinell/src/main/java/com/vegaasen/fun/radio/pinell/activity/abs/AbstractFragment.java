package com.vegaasen.fun.radio.pinell.activity.abs;

import android.support.v4.app.Fragment;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * Represents an empty abstract entity
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public abstract class AbstractFragment extends Fragment {

    protected PinellService getPinellService() {
        return ApplicationContext.INSTANCE.getPinellService();
    }
}
