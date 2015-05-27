package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 27.5.2015
 */
public class InputSourceFragment extends AbstractFragment {

    private static final String TAG = InputSourceFragment.class.getSimpleName();

    private View inputSourceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inputSourceView = inflater.inflate(R.layout.fragment_input_sources, container, false);
        if (inputSourceView == null) {
            Log.e(TAG, "For some reason, the view were unable to be found. Dying");
            throw new RuntimeException("Missing required view in the initialization of the application");
        }
        return inputSourceView;
    }
}
