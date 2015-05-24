package com.vegaasen.fun.radio.pinell.activity.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.adapter.DeviceInformationAdapter;
import com.vegaasen.fun.radio.pinell.model.PinellProperties;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Representation of the information regarding the selected device is represented through this fragment
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 29.03.2015
 */
public class InformationFragment extends AbstractFragment {

    private static final String TAG = InformationFragment.class.getSimpleName();

    private View informationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        informationView = inflater.inflate(R.layout.fragment_information, container, false);
        configureBehaviors();
        refreshDeviceInformation();
        return informationView;
    }

    public void refreshDeviceInformation() {
        if (informationView == null) {
            Log.w(TAG, "Unable to refresh device information. It seems like the view is nilled for some reason.");
            return;
        }
        final ListView deviceOverview = (ListView) informationView.findViewById(R.id.listDeviceInformation);
        if (deviceOverview != null) {
            final Map<String, String> information = generateMapOfDeviceInformation();
            if (deviceOverview.getAdapter() == null) {
                DeviceInformationAdapter deviceInformationAdapter = new DeviceInformationAdapter(information, informationView.getContext());
                deviceOverview.setAdapter(deviceInformationAdapter);
            } else {
                Log.d(TAG, "Refreshing existing device information");
                DeviceInformationAdapter adapter = (DeviceInformationAdapter) deviceOverview.getAdapter();
                adapter.updateDeviceInformation(information);
                adapter.notifyDataSetChanged();
//                deviceOverview.deferNotifyDataSetChanged();
            }
        }
    }

    private void configureBehaviors() {
        Log.d(TAG, "Configuring behaviors for the information view");
        if (informationView != null) {
            configurePowerSwitch();
        }
    }

    private void configurePowerSwitch() {
        Log.d(TAG, "Configuring the powerSwitch");
        final Switch powerSwitch = (Switch) informationView.findViewById(R.id.listDeviceInformationPowerSwitcher);
        if (powerSwitch != null) {
            powerSwitch.setChecked(getPinellService().isPoweredOn());
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

    private Map<String, String> generateMapOfDeviceInformation() {
        if (!getPinellService().isHostConfigured()) {
            return Collections.emptyMap();
        }
        Host host = getPinellService().getSelectedHost();
        Map<String, String> information = new TreeMap<>();
        information.put(PinellProperties.CODE.getKey(), host.getCode());
        information.put(PinellProperties.HOST.getKey(), host.getHost());
        information.put(PinellProperties.PORT.getKey(), Integer.toString(host.getPort()));
        information.put(PinellProperties.CONNECTION_STRING.getKey(), host.getConnectionString());
        if (host.getRadioSession() != null) {
            information.put(PinellProperties.RADIO_SESSION.getKey(), host.getRadioSession().toString());
        }
        if (host.getDeviceInformation() != null) {
            information.put(PinellProperties.DEVICE_NAME.getKey(), host.getDeviceInformation().getName());
            information.put(PinellProperties.DEVICE_VERSION.getKey(), host.getDeviceInformation().getVersion());
            information.put(PinellProperties.DEVICE_API.getKey(), host.getDeviceInformation().getApiUrl());
        }
        return information;
    }

}
