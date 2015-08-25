package com.vegaasen.fun.radio.pinell.activity.fragment.functions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

/**
 * Representation of the information regarding the selected device is represented through this fragment
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 29.03.2015
 */
public class InformationFragment extends AbstractFragment {

    private static final String TAG = InformationFragment.class.getSimpleName();

    private View informationView;
    private TextView listDeviceInformationSoundLevelText;
    private TextView listDeviceInformationPinellInformationText;
    private TextView listDeviceInformationPinellHostNameText;
    private TextView listDeviceInformationPinellHostVersionText;
    private Switch powerSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!getPinellService().isPinellDevice()) {
            informationView = inflater.inflate(R.layout.fragment_pinell_na, container, false);
        } else {
            informationView = inflater.inflate(R.layout.fragment_information, container, false);
            changeActiveContent(container);
            configureElements();
            configureElementValues();
            configureBehaviors();
            refreshDeviceInformation();
        }
        return informationView;
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
        changeCurrentActiveApplicationContextContent(container, R.drawable.ic_radio_black, R.string.sidebarInformation);
    }

    private void configureElements() {
        powerSwitch = (Switch) informationView.findViewById(R.id.listDeviceInformationPowerSwitcher);
        listDeviceInformationSoundLevelText = (TextView) informationView.findViewById(R.id.listDeviceInformationSoundLevelText);
        listDeviceInformationPinellInformationText = (TextView) informationView.findViewById(R.id.listDeviceInformationPinellInformationText);
        listDeviceInformationPinellHostNameText = (TextView) informationView.findViewById(R.id.listDeviceInformationPinellHostNameText);
        listDeviceInformationPinellHostVersionText = (TextView) informationView.findViewById(R.id.listDeviceInformationPinellHostVersionText);
    }

    private void configureBehaviors() {
        Log.d(TAG, "Configuring behaviors for the information view");
        if (informationView != null) {
            configurePowerSwitch();
        }
    }

    private void configureElementValues() {
        final DeviceAudio audioLevels = getPinellService().getAudioLevels();
        final Host host = getPinellService().getSelectedHost();
        if (host == null) {
            allUnavailable();
            return;
        }
        final DeviceInformation deviceInformation = host.getDeviceInformation();
        listDeviceInformationSoundLevelText.setText(audioLevels == null ?
                getUnavailableString() :
                String.format("%s/%s", Integer.toString(audioLevels.getLevel()), getString(R.integer.volumeControlMax)));
        listDeviceInformationPinellInformationText.setText(host.getHost());
        if (deviceInformation != null) {
            listDeviceInformationPinellHostNameText.setText(getSafeString(deviceInformation.getName()));
            listDeviceInformationPinellHostVersionText.setText(getSafeString(deviceInformation.getVersion()));
        } else {
            pinellUnavailable();
        }
    }

    private void allUnavailable() {
        pinellUnavailable();
        listDeviceInformationPinellInformationText.setText(getUnavailableString());
        listDeviceInformationSoundLevelText.setText(getUnavailableString());
    }

    private void pinellUnavailable() {
        listDeviceInformationPinellHostNameText.setText(getUnavailableString());
        listDeviceInformationPinellHostVersionText.setText(getUnavailableString());
    }

    private void configurePowerSwitch() {
        Log.d(TAG, "Configuring the powerSwitch");
        if (powerSwitch != null) {
            //FIXME: some kind of bug here
            final boolean poweredOn = getPinellService().isPoweredOn();
            powerSwitch.post(new Runnable() {
                @Override
                public void run() {
                    powerSwitch.setChecked(poweredOn);
                }
            });
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
    }

    private void postActivities() {
        Log.d(TAG, "Activating postActivities - e.g disabling functions and so on");
        powerSwitch = (Switch) informationView.findViewById(R.id.listDeviceInformationPowerSwitcher);
        final boolean enabled = getPinellService().isPinellDevice();
        if (enabled) {
            return;
        }
        Log.d(TAG, "The current host is not an actual Pinell host. Disabling the powerSwitch selector");
        powerSwitch.setClickable(false);
    }

}
