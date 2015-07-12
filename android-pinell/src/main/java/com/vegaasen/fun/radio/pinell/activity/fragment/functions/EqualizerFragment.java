package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;

/**
 * This fragment holds the information regarding the various equalizers available on the selected Pinell device
 * It handles the following action points:
 * - Lists all equalizers
 * - Select new equalizer
 * - Fetches the current selected equalizer
 * <p/>
 * It does not:
 * Support redefinition of existing "My Eq"
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 27.5.2015
 */
public class EqualizerFragment extends AbstractFragment {

    private static final String TAG = EqualizerFragment.class.getSimpleName();

    private View equalizerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        equalizerView = inflater.inflate(R.layout.fragment_equalizer, container, false);
        if (equalizerView == null) {
            Log.e(TAG, "For some reason, the view were unable to be found. Dying");
            throw new RuntimeException("Missing required view in the initialization of the application");
        }
        return equalizerView;
    }


}
