package com.vegaasen.fun.radio.pinell.activity.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.R;

/**
 * Simple dialog that pops up whenever Wifi has been disabled
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 28.7.2015
 */
public class EnableWifiDialogFragment extends DialogFragment {

    private static final String TAG = EnableWifiDialogFragment.class.getSimpleName();

    public EnableWifiDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialogWifiEnable)
                .setMessage(R.string.dialogWifiContent)
                .setPositiveButton(R.string.dialogWifiEnable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Starting the Wifi-pick activity");
                        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                    }
                })
                .setNegativeButton(R.string.dialogWifiCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.w(TAG, "User did not comply with enabling the Wifi. Exiting application");
                        getActivity().finish();
                    }
                })
                .create();
    }
}
