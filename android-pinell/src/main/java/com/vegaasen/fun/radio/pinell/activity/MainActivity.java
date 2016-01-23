package com.vegaasen.fun.radio.pinell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.SplashScreenFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.BrowseFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.EqualizerFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.InformationFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.InputSourceFragment;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.NowPlayingFragment;
import com.vegaasen.fun.radio.pinell.activity.hidden.HiddenMenuActivity;
import com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity;
import com.vegaasen.fun.radio.pinell.async.function.UpdateAudioLevelAsync;
import com.vegaasen.fun.radio.pinell.async.function.UpdateRadioModeAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

/**
 * This is the main activity which controls all the various fragments within the application itself.
 * It also (by default) initializes the "SelectHostActivity" in the startup of the application.
 * Please note that the SelectHostActivity does not spin or search for hosts itself for the moment. This will be fixed in the near future.
 *
 * @author vegaasen
 * @version 26.7.2015
 * @since 0.1-SNAPSHOT
 */
public class MainActivity extends AbstractActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TRANSIT_FRAGMENT_FADE = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
    private static final int REQUEST_CODE = 1;
    private static final long REFRESH_PERIOD = TimeUnit.SECONDS.toMillis(60);
    private static boolean active, scheduled;

    private int clickedHiddenDoor;

    private SplashScreenFragment splashScreen;
    private NowPlayingFragment nowPlayingFragment;
    private BrowseFragment browseFragment;
    private InputSourceFragment inputSourceFragment;
    private EqualizerFragment equalizerFragment;
    private InformationFragment informationFragment;
    private AbstractFragment activeFragment;
    private RelativeLayout sectionPlaying, sectionBrowse, sectionSource, sectionEqualizer, sectionInformation;
    private RelativeLayout drawerContainer;
    private SlidingPaneLayout componentSlidingSidebar;
    private View currentActiveFragmentView;
    private ImageButton buttonChangePinellHost;
    private ImageView sidebarBranding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "### Remeber to reactivate ALL Fragments ###");
        setContentView(R.layout.main);
        configureCoreElements();
        configureUiElements();
        configureButtonActionListeners();
        if (renderSelectPinellHost()) {
            configureSidebarActionListeners();
        }
        configureScheduledTasks(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureScheduledTasks(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        configureScheduledTasks(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, String.format("Response from request {%s} was {%s}", requestCode, resultCode));
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            configureCurrentInputSource();
            buttonChangePinellHost.setBackgroundResource(R.drawable.ic_cast_connected_white);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentReplacer, informationFragment).commit();
            //todo: Is this even required?
            informationFragment.refreshView();
            setActiveFragmentLayout(currentActiveFragmentView);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (ApplicationContext.INSTANCE.isRadioConnected() && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            new UpdateAudioLevelAsync(getPinellService(), true).execute();
            conditionallyUpdateFragment();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (ApplicationContext.INSTANCE.isRadioConnected() && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            new UpdateAudioLevelAsync(getPinellService(), false).execute();
            conditionallyUpdateFragment();
        }
        return true;
    }

    private void configureScheduledTasks(boolean start) {
        active = start;
        if (!scheduled) {
            if (active) {
                TaskScheduler timer = new TaskScheduler();
                timer.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (active) {
//                            new GetNotifiesAsync(getPinellService()).execute();
                        }
                    }
                }, REFRESH_PERIOD);
            }
            scheduled = true;
        }
    }

    private void configureCurrentInputSource() {
        new UpdateRadioModeAsync(getPinellService()).execute();
    }

    private void conditionallyUpdateFragment() {
        if (activeFragment instanceof InformationFragment) {
            ((InformationFragment) activeFragment).updateSoundLevel();
        }
    }

    private void configureCoreElements() {
    }

    private void configureUiElements() {
        splashScreen = new SplashScreenFragment();
        nowPlayingFragment = new NowPlayingFragment();
        browseFragment = new BrowseFragment();
        inputSourceFragment = new InputSourceFragment();
        equalizerFragment = new EqualizerFragment();
        informationFragment = new InformationFragment();
        sidebarBranding = (ImageView) findViewById(R.id.imgSidebarBranding);
        sectionPlaying = (RelativeLayout) findViewById(R.id.sectionPlaying);
        sectionBrowse = (RelativeLayout) findViewById(R.id.sectionBrowse);
        sectionSource = (RelativeLayout) findViewById(R.id.sectionSource);
        sectionEqualizer = (RelativeLayout) findViewById(R.id.sectionEqualizer);
        sectionInformation = (RelativeLayout) findViewById(R.id.sectionInformation);
        buttonChangePinellHost = (ImageButton) findViewById(R.id.btnCurrentApplicationSelectDevice);
        drawerContainer = (RelativeLayout) findViewById(R.id.lstCurrentApplicationDrawerContainer);
        componentSlidingSidebar = (SlidingPaneLayout) findViewById(R.id.componentSlidingSidebar);
    }

    private void configureButtonActionListeners() {
        final MainActivity activity = this;
        buttonChangePinellHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (renderSelectPinellHost()) {
                    configureSidebarActionListeners();
                }
            }
        });
        drawerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (componentSlidingSidebar.isOpen()) {
                    componentSlidingSidebar.closePane();
                } else {
                    componentSlidingSidebar.openPane();
                }
            }
        });
        sidebarBranding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedHiddenDoor > 3) {
                    clickedHiddenDoor = 0;
                    startActivityForResult(new Intent(activity, HiddenMenuActivity.class), REQUEST_CODE);
                } else {
                    Toast.makeText(getBaseContext(), String.format("%s pow!", clickedHiddenDoor), Toast.LENGTH_SHORT).show();
                    clickedHiddenDoor++;
                }
            }
        });
    }

    private void configureSidebarActionListeners() {
        configureSidebarFragment(sectionPlaying, nowPlayingFragment);
        configureSidebarFragment(sectionBrowse, browseFragment);
        configureSidebarFragment(sectionSource, inputSourceFragment);
        configureSidebarFragment(sectionEqualizer, equalizerFragment);
        configureSidebarFragment(sectionInformation, informationFragment);
    }

    private void configureSidebarFragment(final RelativeLayout candidate, final AbstractFragment fragment) {
        candidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentReplacer, fragment).commit();
                setActiveFragment(fragment);
                setActiveFragmentLayout(v);
            }
        });
    }

    private void setActiveFragmentLayout(final View candidate) {
        if (candidate != null) {
            if (candidate == currentActiveFragmentView) {
                Log.d(TAG, "Detected same candidate as the current selected active fragment. Skipping color changes");
                return;
            }
            if (currentActiveFragmentView != null) {
                Log.d(TAG, String.format("The currentActiveFragmentView {%s} is not undefined and will be set back to the original defined color", currentActiveFragmentView.getId()));
                currentActiveFragmentView.setBackgroundColor(getResources().getColor(R.color.newSidebarBoxBackgroundColor));
            }
            currentActiveFragmentView = candidate;
            currentActiveFragmentView.setBackgroundColor(getResources().getColor(R.color.sidebarBoxSelectedColor));
        }
    }

    private void setActiveFragment(AbstractFragment fragment) {
        activeFragment = fragment;
    }

    private boolean renderSelectPinellHost() {
        renderDefaultSplashScreen();
        Log.e(TAG, "WIFI CHECK DISABLED!! WARNING!!!!!!");
//        if (!isWifiEnabledAndConnected() && !isConnectedToSomeDevice()) {
//            final EnableWifiDialogFragment enableWifiDialogFragment = new EnableWifiDialogFragment();
//            enableWifiDialogFragment.show(getFragmentManager(), TAG);
//            return false;
//        }
        startActivityForResult(new Intent(this, SelectHostActivity.class), REQUEST_CODE);
        return true;
    }

    private void renderDefaultSplashScreen() {
        Log.i(TAG, String.format("Configuring the transit to use {%s} as the transition style (see FragmentTransaction for more details)", TRANSIT_FRAGMENT_FADE));
        getSupportFragmentManager().beginTransaction().setTransitionStyle(TRANSIT_FRAGMENT_FADE).commit();
        Log.d(TAG, "Displaying splashScreen for the application");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentReplacer, splashScreen).commit();
    }

}
