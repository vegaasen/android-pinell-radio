package com.vegaasen.fun.radio.pinell.activity.host;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.google.common.collect.Lists;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.adapter.DeviceArrayAdapter;
import com.vegaasen.fun.radio.pinell.listner.DeviceListListener;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.List;

/**
 * This represents the selectable host activity. Its main objective is just to let the user select which host that he/she would like to connect to.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 0.1
 * @see DeviceListListener
 * @since 29.03.2015
 */
public class SelectHostActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_device_chooser);
        configureButtons();
    }

    private void configureButtons() {
        final ImageButton refreshDevices = (ImageButton) findViewById(R.id.btnDialogRefreshDevices);
        final Activity currentActivity = this;
        refreshDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, R.string.devices_refreshing, Toast.LENGTH_SHORT).show();
                getPinellService().getPinellConnection(true);
                refreshDevicesToList(currentActivity);
            }
        });
    }

    private void refreshDevicesToList(Activity activity) {
        final ListView deviceOverview = (ListView) findViewById(R.id.listDevices);
        if (deviceOverview != null) {
            final List<Host> pinellHosts = Lists.newArrayList(getPinellService().getPinellHosts());
            if (deviceOverview.getAdapter() == null) {
                final DeviceArrayAdapter devicesAdapter = new DeviceArrayAdapter(context, R.layout.device_listview, pinellHosts);
                deviceOverview.setAdapter(devicesAdapter);
                deviceOverview.setOnItemClickListener(new DeviceListListener(activity, getPinellService()));
            } else {
                deviceOverview.deferNotifyDataSetChanged();
            }
        }
    }

}
