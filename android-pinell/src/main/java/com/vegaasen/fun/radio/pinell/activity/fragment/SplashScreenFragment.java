package com.vegaasen.fun.radio.pinell.activity.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;

/**
 * Simple splashscreen for the Pinell application. Nothing fancy, nothing special.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 27.5.2015
 */
public class SplashScreenFragment extends AbstractFragment {

    private static final String TAG = SplashScreenFragment.class.getSimpleName();

    private View splashScreenView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (splashScreenView == null) {
            splashScreenView = inflater.inflate(R.layout.fragment_splash, container, false);
        }
        if (splashScreenView == null) {
            Log.e(TAG, "For some reason, the splashScreen were unable to be found. Dying");
            throw new RuntimeException("Missing required splashScreen in the initialization of the application");
        }
        return splashScreenView;
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_radio_white, R.string.splashScreenWelcome);
    }
}
