package com.vegaasen.fun.radio.pinell.activity.abs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import com.google.common.collect.Lists;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.adapter.DeviceArrayAdapter;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.fun.radio.pinell.service.impl.PinellServiceImpl;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.List;

/**
 * Simple layer abstraction for all common functionality
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public abstract class AbstractActivity extends Activity {

    private static final PinellService PINELL_SERVICE = new PinellServiceImpl();

    protected final Context context = this;

    protected void generateDeviceConnectionDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_device_chooser);
        refreshDevicesToList(dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void refreshDevicesToList(Dialog dialogView) {
        final ListView deviceOverview = (ListView) dialogView.findViewById(R.id.listDevices);
        if (deviceOverview != null) {
            final List<Host> pinellHosts = Lists.newArrayList(Host.create("localhost", 1234, "123ads"));
            final DeviceArrayAdapter devicesAdapter = new DeviceArrayAdapter(context, R.layout.device_listview, pinellHosts);
            deviceOverview.setAdapter(devicesAdapter);
        }
    }

    protected static PinellService getPinellService() {
        if (PINELL_SERVICE != null) {
            return PINELL_SERVICE;
        }
        throw new RuntimeException("PinellService is nilled for some reason. Dying :).");
    }

}
