package com.vegaasen.fun.radio.pinell.activity.abs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.base.Strings;
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
    private static final int MAX_SAFE_CHARS = 22;
    private static final String ADDITION = "...";

    public String getApplicationVersion() {
        try {
            final String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            return Strings.isNullOrEmpty(versionName) ? getUnavailableString() : versionName;
        } catch (Exception e) {
            //*gulp*
        }
        return getUnavailableString();
    }

    public static String getSafeString(final String candidate) {
        return getSafeString(candidate, MAX_SAFE_CHARS);
    }

    protected PinellService getPinellService() {
        return ApplicationContext.INSTANCE.getPinellService();
    }

    protected static String getSafeString(final String candidate, int length) {
        return candidate.length() > length ? candidate.substring(0, length) + ADDITION : candidate;
    }

    protected String getUnavailableString() {
        return getString(R.string.genericUnknown);
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

    protected boolean isWifiEnabledAndConnected() {
        if (!ApplicationContext.INSTANCE.getWifiManager().isWifiEnabled()) {
            Log.d(TAG, "Wifi is not enabled");
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Log.d(TAG, "The connectionManager is nilled");
            return false;
        }
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && wifiInfo.isConnected();
    }

}
