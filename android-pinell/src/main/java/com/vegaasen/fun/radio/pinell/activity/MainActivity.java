package com.vegaasen.fun.radio.pinell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.activity.fragment.InformationFragment;
import com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;

/**
 * This is the main activity which controls all the various fragments within the application itself.
 * It also (by default) initalizes the "SelectHostActivity" in the startup of the application.
 *
 * @author vegaasen
 * @since 0.1-SNAPSHOT
 */
public class MainActivity extends AbstractActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1;

    private InformationFragment deviceInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        configureUiElements();
        final Intent intent = new Intent(this, SelectHostActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, String.format("Response from request {%s} was {%s}", requestCode, resultCode));
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "Woppey!");
            deviceInformation.refreshDeviceInformation();
        }
    }

    private void configureUiElements() {
        deviceInformation = (InformationFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentInformation);
    }

}
