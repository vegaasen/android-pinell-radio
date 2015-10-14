package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.util.scheduler.TaskScheduler;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.concurrent.TimeUnit;

/**
 * Representation of the information regarding the selected device is represented through this fragment
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 29.03.2015
 */
public class InformationFragment extends AbstractFragment {

    private static final String TAG = InformationFragment.class.getSimpleName();

    private static boolean active, scheduled;

    private View informationView;
    private Switch powerSwitch;
    private TextView currentSoundLevel, currentPinellHost, currentPinellInputSource, currentApplicationVersion;
    private RelativeLayout hostInformation, informationApplicationVersion;
    private boolean pinellDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeActiveContent(container);
//        if (!isWifiEnabledAndConnected()) {
//            informationView = inflater.inflate(R.layout.fragment_pinell_network_offline, container, false);
//            pinellDevice = false;
//        } else
        if (!getPinellService().isPinellDevice()) {
            informationView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
            pinellDevice = false;
        } else {
            informationView = inflater.inflate(R.layout.fragment_information, container, false);
            configureElements();
            configureElementValues();
            configureBehaviors();
            refreshDeviceInformation();
            pinellDevice = true;
        }
        return informationView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pinellDevice) {
            configureScheduledTasks(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        configureScheduledTasks(false);
    }

    public void updateSoundLevel() {
        updateSoundLevel(getPinellService().getAudioLevels());
    }

    public void updateSoundLevel(DeviceAudio audioLevels) {
        currentSoundLevel.setText(audioLevels == null ? getUnavailableString() : String.format("%s of %s", Integer.toString(audioLevels.getLevel()), getString(R.integer.volumeControlMax)));
    }

    public void refreshDeviceInformation() {
        if (informationView == null) {
            Log.w(TAG, "Unable to refresh device information. It seems like the view is nilled for some reason.");
            return;
        }
        postActivities();
    }

    @Override
    protected void changeActiveContent(ViewGroup container) {
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_radio_white, R.string.sidebarInformation);
    }

    private void configureScheduledTasks(boolean start) {
        Log.d(TAG, String.format("Running the tasks? {%s}", start));
        active = start;
        if (!scheduled) {
            if (start) {
                TaskScheduler timer = new TaskScheduler();
                timer.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (active) {
                            Log.d(TAG, "Refreshing the information");
                            configureElementValues();
                            configurePowerSwitchPosition();
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(25));
            }
            scheduled = true;
        }
    }

    private void configureElements() {
        powerSwitch = (Switch) informationView.findViewById(R.id.listDeviceInformationPowerSwitcher);
        currentSoundLevel = (TextView) informationView.findViewById(R.id.informationVolumeLevelTxt);
        currentPinellHost = (TextView) informationView.findViewById(R.id.informationHostTxt);
        currentPinellInputSource = (TextView) informationView.findViewById(R.id.informationCurrentInputSourceTxt);
        currentApplicationVersion = (TextView) informationView.findViewById(R.id.informationApplicationVersionTxt);
        hostInformation = (RelativeLayout) informationView.findViewById(R.id.informationHost);
        informationApplicationVersion = (RelativeLayout) informationView.findViewById(R.id.informationApplicationVersion);
    }

    private void configureBehaviors() {
        Log.d(TAG, "Configuring behaviors for the information view");
        if (informationView != null) {
            configurePowerSwitch();
        }
        hostInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.messageHostDetailsTitle)
                        .setMessage(assembleHostText())
                        .show();
            }
        });
        informationApplicationVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.messageApplicationDetailsTitle)
                        .setMessage(assembleApplicationText())
                        .show();
            }
        });
    }

    private void configureElementValues() {
        final Host host = getPinellService().getSelectedHost();
        if (host == null) {
            allUnavailable();
            return;
        }
        final RadioMode currentInputSource = getPinellService().getCurrentInputSource();
        if (currentInputSource != null && !Strings.isNullOrEmpty(currentInputSource.getName())) {
            currentPinellInputSource.setText(currentInputSource.getName());
        }
        final DeviceInformation deviceInformation = host.getDeviceInformation();
        if (deviceInformation != null) {
            currentPinellHost.setText(getSafeString(deviceInformation.getName()));
        } else {
            pinellUnavailable();
        }
        currentApplicationVersion.setText(getApplicationVersion());
        updateSoundLevel();
    }

    private void allUnavailable() {
        pinellUnavailable();
        currentSoundLevel.setText(getUnavailableString());
    }

    private void pinellUnavailable() {
        currentPinellHost.setText(getUnavailableString());
    }

    private void configurePowerSwitch() {
        configurePowerSwitchPosition();
        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getPinellService().setPowerState(PowerState.ON);
                } else {
                    getPinellService().setPowerState(PowerState.OFF);
                }
            }
        });
    }

    private void configurePowerSwitchPosition() {
        final boolean poweredOn = getPinellService().isPoweredOn();
        powerSwitch.post(new Runnable() {
            @Override
            public void run() {
                powerSwitch.setChecked(poweredOn);
            }
        });
    }

    private void postActivities() {
        Log.d(TAG, "Activating postActivities - e.g disabling functions and so on");
        if (powerSwitch != null) {
            final boolean enabled = getPinellService().isPinellDevice();
            if (enabled) {
                return;
            }
            Log.d(TAG, "The current host is not an actual Pinell host. Disabling the powerSwitch selector");
        }
    }

    private String assembleHostText() {
        final Host selectedHost = getPinellService().getSelectedHost();
        return String.format("{'hostIp':'%s', 'sessionId':'%s'}\n:-)", selectedHost.getHost(), selectedHost.getRadioSession().getId());
    }

    private String assembleApplicationText() {
        return String.format("{'applicationVersion':'%s', 'author':'%s', 'authorEmail':'%s'}", getApplicationVersion(), "", "");
    }

}
