package com.vegaasen.fun.radio.pinell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.activity.fragment.SplashScreenFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.BrowseFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.EqualizerFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.InformationFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.InputSourceFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.NowPlayingFragment;
import com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity;

/**
 * This is the main activity which controls all the various fragments within the application itself.
 * It also (by default) initializes the "SelectHostActivity" in the startup of the application.
 * Please note that the SelectHostActivity does not spin or search for hosts itself for the moment. This will be fixed in the near future.
 *
 * @author vegaasen
 * @since 0.1-SNAPSHOT
 */
public class MainActivity extends AbstractActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TRANSIT_FRAGMENT_FADE = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
    private static final int REQUEST_CODE = 1;

    private SplashScreenFragment splashScreen;
    private NowPlayingFragment nowPlayingFragment;
    private BrowseFragment browseFragment;
    private InputSourceFragment inputSourceFragment;
    private EqualizerFragment equalizerFragment;
    private InformationFragment informationFragment;
    private RelativeLayout sectionPlaying, sectionBrowse, sectionSource, sectionEqualizer, sectionInformation;
    private RelativeLayout currentActiveLayout;

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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentReplacer, informationFragment).commit();
            informationFragment.refreshDeviceInformation();
        }
    }

    private void configureUiElements() {
        splashScreen = new SplashScreenFragment();
        nowPlayingFragment = new NowPlayingFragment();
        browseFragment = new BrowseFragment();
        inputSourceFragment = new InputSourceFragment();
        equalizerFragment = new EqualizerFragment();
        informationFragment = new InformationFragment();
        sectionPlaying = (RelativeLayout) findViewById(R.id.sectionPlaying);
        sectionBrowse = (RelativeLayout) findViewById(R.id.sectionBrowse);
        sectionSource = (RelativeLayout) findViewById(R.id.sectionSource);
        sectionEqualizer = (RelativeLayout) findViewById(R.id.sectionEqualizer);
        sectionInformation = (RelativeLayout) findViewById(R.id.sectionInformation);
    }

    private void configureSidebarActionListeners() {
        sectionPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentReplacer, nowPlayingFragment).commit();
            }
        });
        sectionInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentReplacer, informationFragment).commit();
            }
        });
    }

    private void setActiveFragmentLayout(final RelativeLayout candidate) {
        if (candidate != null) {
            if (currentActiveLayout != null) {
                Log.d(TAG, String.format("The currentActiveLayout {%s} is not undefined and will be set back to the original defined color", currentActiveLayout.getId()));
                currentActiveLayout.setBackgroundColor(getResources().getColor(R.color.sidebarBoxColor));
            }
            currentActiveLayout = candidate;
            currentActiveLayout.setBackgroundColor(getResources().getColor(R.color.sidebarBoxSelectedColor));
        }
    }

    private void renderSplashScreen() {
        Log.i(TAG, String.format("Configuring the transit to use {%s} as the transition style (see FragmentTransaction for more details)", TRANSIT_FRAGMENT_FADE));
        getSupportFragmentManager().beginTransaction().setTransitionStyle(TRANSIT_FRAGMENT_FADE).commit();
        Log.d(TAG, "Displaying splashScreen for the application");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentReplacer, splashScreen).commit();
    }

}
