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

    private static final int MAX_SAFE_CHARS = 17;
    public static final String ADDITION = "...";

    protected PinellService getPinellService() {
        return ApplicationContext.INSTANCE.getPinellService();
    }

    protected static String getSafeString(final String candidate) {
        return getSafeString(candidate, MAX_SAFE_CHARS);
    }

    protected static String getSafeString(final String candidate, int length) {
        return candidate.length() > length ? candidate.substring(0, length) + ADDITION : candidate;
    }

}
