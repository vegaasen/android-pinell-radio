package com.vegaasen.fun.radio.pinell.activity.abs;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * Represents an empty abstract entity
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public abstract class AbstractFragment extends Fragment {

    private static final String TAG = AbstractFragment.class.getSimpleName();
    private static final int MAX_SAFE_CHARS = 17;
    private static final String ADDITION = "...";

    protected PinellService getPinellService() {
        return ApplicationContext.INSTANCE.getPinellService();
    }

    protected static String getSafeString(final String candidate) {
        return getSafeString(candidate, MAX_SAFE_CHARS);
    }

    protected static String getSafeString(final String candidate, int length) {
        return candidate.length() > length ? candidate.substring(0, length) + ADDITION : candidate;
    }

    protected String getUnavailableString() {
        return getString(R.string.unavailable);
    }

    protected abstract void changeActiveContent(ViewGroup container);

    protected void changeCurrentActiveApplicationContextContent(ViewGroup container, final int candidateIcon, final int candidateText) {
        if (container == null || container.getRootView() == null) {
            Log.d(TAG, "It seems like the provided viewGroupContainer is nilled for some reason. Skipping the activeIcon change request");
            return;
        }
        ImageView currentApplicationContextActiveIcon = (ImageView) container.getRootView().findViewById(R.id.imgCurrentApplicationContext);
        TextView currentApplicationContextActiveLabel = (TextView) container.getRootView().findViewById(R.id.txtCurrentApplicationContext);
        if (currentApplicationContextActiveIcon == null) {
            Log.w(TAG, "Both the container and the rootView was found. However it seems like the imageView was nilled");
            return;
        }
        currentApplicationContextActiveIcon.setImageResource(candidateIcon);
        if (currentApplicationContextActiveLabel == null) {
            return;
        }
        currentApplicationContextActiveLabel.setText(candidateText);
    }

}
