package com.vegaasen.fun.radio.pinell.discovery.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.discovery.model.NetInfo;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_IP_END;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_IP_START;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_PORT_END;
import static com.vegaasen.fun.radio.pinell.common.Constants.DEFAULT_PORT_START;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_CIDR_CUSTOM;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_DONATE;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_EMAIL;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_INTF;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_IP_CUSTOM;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_IP_END;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_IP_START;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_PORT_END;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_PORT_START;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_RATECTRL_ENABLE;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_TIMEOUT_DISCOVER;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_VERSION;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_WEBSITE;
import static com.vegaasen.fun.radio.pinell.common.Constants.KEY_WIFI;

/**
 * @author vegaasen
 * @author unknown
 */
public class Prefs extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = Prefs.class.getSimpleName();

    private static final String URL_DONATE = "paypal-link";
    private static final String URL_WEB = "github-link";
    public static final String MAIL_TYPE = "plain/text";

    private Context context;
    private PreferenceScreen ps = null;
    private String before_ip_start;
    private String before_ip_end;
    private String before_port_start;
    private String before_port_end;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        ps = getPreferenceScreen();
        ps.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        // Default state of checkboxes
        checkTimeout(KEY_TIMEOUT_DISCOVER, KEY_RATECTRL_ENABLE, false);
        // Before change values
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        before_ip_start = prefs.getString(KEY_IP_START, DEFAULT_IP_START);
        before_ip_end = prefs.getString(KEY_IP_END, DEFAULT_IP_END);
        before_port_start = prefs.getString(KEY_PORT_START, DEFAULT_PORT_START);
        before_port_end = prefs.getString(KEY_PORT_END, DEFAULT_PORT_END);

        // Interfaces list
        ListPreference intf = (ListPreference) ps.findPreference(KEY_INTF);
        try {
            ArrayList<NetworkInterface> nis = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            final int len = nis.size();
            // If there's more than just 2 interfaces (local + network)
            if (len > 2) {
                String[] intf_entries = new String[len - 1];
                String[] intf_values = new String[len - 1];
                int i = 0;
                for (NetworkInterface ni : nis) {
                    if (!ni.getName().equals("lo")) {
                        intf_entries[i] = ni.getDisplayName();
                        intf_values[i] = ni.getName();
                        i++;
                    }
                }
                intf.setEntries(intf_entries);
                intf.setEntryValues(intf_values);
            } else {
                intf.setEnabled(false);
            }
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
            intf.setEnabled(false);
        }

        // Wifi settings listener
        ps.findPreference(KEY_WIFI)
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        return true;
                    }
                });

        // Donate click listener
        ps.findPreference(KEY_DONATE)
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(URL_DONATE));
                        startActivity(i);
                        return true;
                    }
                });

        // Website
        Preference website = ps.findPreference(KEY_WEBSITE);
        website.setSummary(URL_WEB);
        website.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(URL_WEB));
                startActivity(i);
                return true;
            }
        });

        // Contact
        Preference contact = ps.findPreference(KEY_EMAIL);
        final String email = getString(R.string.me_email), subject = getString(R.string.me_email_subject);
        contact.setSummary(email);
        contact.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType(MAIL_TYPE);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    //meh
                }
                return true;
            }
        });

        // Version
        Preference version = ps.findPreference(KEY_VERSION);
        try {
            version.setSummary(getPackageManager().getPackageInfo(Prefs.class.getPackage().getName(), 0).versionName);
        } catch (NameNotFoundException e) {
            version.setSummary("unknown");
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch (key) {
            case KEY_PORT_START:
            case KEY_PORT_END:
                checkPortRange();
                break;
            case KEY_IP_START:
            case KEY_IP_END:
                checkIpRange();
                //} else if (key.equals(KEY_NTHREADS)) {
                //    checkMaxThreads();
                break;
            case KEY_RATECTRL_ENABLE:
                checkTimeout(KEY_TIMEOUT_DISCOVER, KEY_RATECTRL_ENABLE, false);
                break;
            case KEY_CIDR_CUSTOM: {
                CheckBoxPreference cb = (CheckBoxPreference) ps.findPreference(KEY_CIDR_CUSTOM);
                if (cb.isChecked()) {
                    ((CheckBoxPreference) ps.findPreference(KEY_IP_CUSTOM)).setChecked(false);
                }
                sendBroadcast(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION));
                break;
            }
            case KEY_IP_CUSTOM: {
                CheckBoxPreference cb = (CheckBoxPreference) ps.findPreference(KEY_IP_CUSTOM);
                if (cb.isChecked()) {
                    ((CheckBoxPreference) ps.findPreference(KEY_CIDR_CUSTOM)).setChecked(false);
                }
                sendBroadcast(new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION));
                break;
            }
        }
    }

    private void checkTimeout(String key_pref, String key_cb, boolean value) {
        EditTextPreference timeout = (EditTextPreference) ps.findPreference(key_pref);
        CheckBoxPreference cb = (CheckBoxPreference) ps.findPreference(key_cb);
        if (cb.isChecked()) {
            timeout.setEnabled(value);
        } else {
            timeout.setEnabled(!value);
        }
    }

    private void checkIpRange() {
        EditTextPreference ipStartEdit = (EditTextPreference) ps.findPreference(KEY_IP_START);
        EditTextPreference ipEndEdit = (EditTextPreference) ps.findPreference(KEY_IP_END);
        // Check if these are valid IP's
        Pattern pattern = Pattern
                .compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))");
        Matcher matcher1 = pattern.matcher(ipStartEdit.getText());
        Matcher matcher2 = pattern.matcher(ipEndEdit.getText());
        if (!matcher1.matches() || !matcher2.matches()) {
            ipStartEdit.setText(before_ip_start);
            ipEndEdit.setText(before_ip_end);
            Toast.makeText(context, R.string.genericUndocumented, Toast.LENGTH_LONG).show();
            return;
        }
        // Check if ip start is bigger or equal than ip end
        try {
            long ipStart = NetInfo.getUnsignedLongFromIp(ipStartEdit.getText());
            long ipEnd = NetInfo.getUnsignedLongFromIp(ipEndEdit.getText());
            if (ipStart > ipEnd) {
                ipStartEdit.setText(before_ip_start);
                ipEndEdit.setText(before_ip_end);
                Toast.makeText(context, R.string.genericUndocumented, Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            ipStartEdit.setText(before_ip_start);
            ipEndEdit.setText(before_ip_end);
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkPortRange() {
        // Check if port start is bigger or equal than port end
        EditTextPreference portStartEdit = (EditTextPreference) ps.findPreference(KEY_PORT_START);
        EditTextPreference portEndEdit = (EditTextPreference) ps.findPreference(KEY_PORT_END);
        try {
            int portStart = Integer.parseInt(portStartEdit.getText());
            int portEnd = Integer.parseInt(portEndEdit.getText());
            if (portStart >= portEnd) {
                portStartEdit.setText(before_port_start);
                portEndEdit.setText(before_port_end);
                Toast.makeText(context, R.string.genericUndocumented, Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            portStartEdit.setText(before_port_start);
            portEndEdit.setText(before_port_end);
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
