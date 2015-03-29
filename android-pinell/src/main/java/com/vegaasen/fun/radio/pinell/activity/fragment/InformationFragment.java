package com.vegaasen.fun.radio.pinell.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractFragment;
import com.vegaasen.fun.radio.pinell.adapter.DeviceInformationAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View informationView = inflater.inflate(R.layout.fragment_information, container, false);
        refreshDeviceInformation(container);
        return informationView;
    }

    private void refreshDeviceInformation(ViewGroup container) {
        final ListView deviceOverview = (ListView) container.findViewById(R.id.listDevices);
        if (deviceOverview != null) {
            if (deviceOverview.getAdapter() == null) {
                DeviceInformationAdapter deviceInformationAdapter = new DeviceInformationAdapter(null);
                deviceOverview.setAdapter(deviceInformationAdapter);
            } else {
                deviceOverview.deferNotifyDataSetChanged();
            }
        }
    }

    private Map<String, String> generateMapOfDeviceInformation() {
        Host host = getPinellService().getSelectedHost();
        if (host == null) {
            return Collections.emptyMap();
        }
        Map<String, String> information = new TreeMap<>();
        //todo: finish of the map of informational stuff :-)
        information.put("code", host.getCode());
        return information;
    }

}
