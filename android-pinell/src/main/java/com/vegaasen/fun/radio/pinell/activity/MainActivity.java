package com.vegaasen.fun.radio.pinell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.RelativeLayout;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.activity.fragment.InformationFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.SplashScreenFragment;
import com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity;

/**
 * This is the main activity which controls all the various fragments within the application itself.
 * It also (by default) initalizes the "SelectHostActivity" in the startup of the application.
 *
 * @author vegaasen
 * @since 0.1-SNAPSHOT
 */
public class MainActivity extends AbstractActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TRANSIT_FRAGMENT_FADE = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
    private static final int REQUEST_CODE = 1;

    private InformationFragment deviceInformation;
    private SplashScreenFragment splashScreen;
    private RelativeLayout sectionPlaying, sectionBrowse, sectionSource, sectionEqualizer, sectionInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        configureUiElements();
        configureSidebarActionListeners();
        renderSplashScreen();
        final Intent intent = new Intent(this, SelectHostActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, String.format("Response from request {%s} was {%s}", requestCode, resultCode));
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //todo: use replace instead of add?
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentReplacer, deviceInformation).commit();
            deviceInformation.refreshDeviceInformation();
        }
    }

    private void configureUiElements() {
        deviceInformation = new InformationFragment();
        splashScreen = new SplashScreenFragment();
        sectionPlaying = (RelativeLayout) findViewById(R.id.sectionPlaying);
        sectionBrowse = (RelativeLayout) findViewById(R.id.sectionBrowse);
        sectionSource = (RelativeLayout) findViewById(R.id.sectionSource);
        sectionEqualizer = (RelativeLayout) findViewById(R.id.sectionEqualizer);
        sectionInformation = (RelativeLayout) findViewById(R.id.sectionInformation);
    }

    private void configureSidebarActionListeners() {

    }

    private void renderSplashScreen() {
        Log.i(TAG, String.format("Configuring the transit to use {%s} as the transition style (see FragmentTransaction for more details)", TRANSIT_FRAGMENT_FADE));
        getSupportFragmentManager().beginTransaction().setTransitionStyle(TRANSIT_FRAGMENT_FADE).commit();
        Log.d(TAG, "Displaying splashScreen for the application");
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentReplacer, splashScreen).commit();
    }

}
