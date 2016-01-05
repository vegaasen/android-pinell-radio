package com.vegaasen.fun.radio.pinell.service.impl;

import android.content.SharedPreferences;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.service.StorageService;
import com.vegaasen.fun.radio.pinell.util.translator.HostTranslator;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharedPreferencesStorageServiceImpl implements StorageService {

    private static final String TAG = SharedPreferencesStorageServiceImpl.class.getSimpleName();

    private final SharedPreferences preferences;

    public SharedPreferencesStorageServiceImpl(SharedPreferences preferences) {
        this.preferences = preferences;
        Log.d(TAG, "Shared preferences set");
    }

    @Override
    public void clear() {
        if (preferences == null) {
            Log.w(TAG, "Unable to clear the existing preferences due to preferences being nilled");
            return;
        }
        preferences.edit().clear().apply();
    }

    @Override
    public void remove(Host hostBean) {
        if (preferences == null) {
            Log.w(TAG, "Unable to remove the host due to preferences being nilled");
            return;
        }
        preferences.edit().remove(HostTranslator.INSTANCE.translate(hostBean)).apply();
    }

    @Override
    public void store(Host candidate) {
        if (preferences == null) {
            Log.w(TAG, "Unable to store to preferences due to preferences being nilled");
            return;
        }
        String translate = HostTranslator.INSTANCE.translate(candidate);
        if (!preferences.contains(translate)) {
            preferences.edit().putString(translate, candidate.getHost()).apply();
            Log.d(TAG, String.format("Stored {%s} to the preferences", translate));
        }
    }

    @Override
    public List<Host> getAll() {
        if (preferences == null) {
            Log.w(TAG, "Unable to fetch any existing hosts as the preferences seems to be nilled");
            return Collections.emptyList();
        }
        List<Host> hosts = new ArrayList<>();
        for (String candidate : preferences.getAll().keySet()) {
            Host translated = HostTranslator.INSTANCE.translate(candidate);
            if (translated != null) {
                hosts.add(translated);
            }
        }
        Log.d(TAG, String.format("Found {%s} existing hosts", hosts.size()));
        return hosts;
    }
}
