package com.vegaasen.fun.radio.pinell.async.information;

import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.fragment.functions.InformationFragment;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.lang.ref.WeakReference;

/**
 * The asyncronious stuff within this class is dedicated to the loading of all parts of the InformationLayout itself
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @see com.vegaasen.fun.radio.pinell.activity.fragment.functions.InformationFragment
 * @since 14.10.2015
 */
public class InformationLoadingAsync extends AbstractFragmentVoidAsync {

    private final WeakReference<InformationFragment> fragment;

    private Switch powerSwitch;
    private TextView currentSoundLevel, currentPinellHost, currentPinellInputSource, currentApplicationVersion;
    private RelativeLayout hostInformation, informationApplicationVersion;
    private ProgressBar spinner;
    private Host host;
    private RadioMode currentInputSource;
    private DeviceAudio audioLevels;
    private boolean isPoweredOn, stop = false;

    public InformationLoadingAsync(FragmentManager fragmentManager, WeakReference<InformationFragment> fragment, View informationView, PinellService pinellService, String unknown) {
        super(fragmentManager, informationView, pinellService, unknown);
        this.fragment = fragment;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        isPoweredOn = pinellService.isPoweredOn();
        host = pinellService.getSelectedHost();
        currentInputSource = pinellService.getCurrentInputSource();
        audioLevels = pinellService.getAudioLevels();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        configureViewComponents();
        if (host == null) {
            allUnavailable();
            return;
        }
        if (stop) {
            return;
        }
        if (currentInputSource != null && !Strings.isNullOrEmpty(currentInputSource.getName())) {
            currentPinellInputSource.setText(currentInputSource.getName());
        }
        DeviceInformation deviceInformation = host.getDeviceInformation();
        if (deviceInformation != null) {
            currentPinellHost.setText(InformationFragment.getSafeString(deviceInformation.getName()));
        } else {
            pinellUnavailable();
        }
        final InformationFragment informationFragment = fragment.get();
        currentApplicationVersion.setText(informationFragment.getApplicationVersion());
        currentSoundLevel.setText(audioLevels == null ? unknown : String.format("%s of %s", Integer.toString(audioLevels.getLevel()), "32")); // getString(R.integer.volumeControlMax)
        spinner.setVisibility(View.GONE);
        hostInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(informationFragment.getActivity())
                        .setTitle(R.string.messageHostDetailsTitle)
                        .setMessage(assembleHostText())
                        .show();
            }
        });
        informationApplicationVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(informationFragment.getActivity())
                        .setTitle(R.string.messageApplicationDetailsTitle)
                        .setMessage(assembleApplicationText())
                        .show();
            }
        });
        powerSwitch.post(new Runnable() {
            @Override
            public void run() {
                powerSwitch.setChecked(isPoweredOn);
            }
        });
        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new InformationPowerStateAsync(pinellService, PowerState.ON).execute();
                } else {
                    new InformationPowerStateAsync(pinellService, PowerState.OFF).execute();
                }
            }
        });
    }

    @Override
    protected void configureViewComponents() {
        powerSwitch = (Switch) view.findViewById(R.id.listDeviceInformationPowerSwitcher);
        currentSoundLevel = (TextView) view.findViewById(R.id.informationVolumeLevelTxt);
        currentPinellHost = (TextView) view.findViewById(R.id.informationHostTxt);
        currentPinellInputSource = (TextView) view.findViewById(R.id.informationCurrentInputSourceTxt);
        currentApplicationVersion = (TextView) view.findViewById(R.id.informationApplicationVersionTxt);
        spinner = (ProgressBar) view.findViewById(R.id.informationSpinner);
        hostInformation = (RelativeLayout) view.findViewById(R.id.informationHost);
        informationApplicationVersion = (RelativeLayout) view.findViewById(R.id.informationApplicationVersion);
    }

    @Override
    protected void onCancelled() {
        stop = true;
        cancel(true);
    }

    private void allUnavailable() {
        pinellUnavailable();
        currentSoundLevel.setText(unknown);
    }

    private void pinellUnavailable() {
        currentPinellHost.setText(unknown);
    }

    private String assembleHostText() {
        final Host selectedHost = pinellService.getSelectedHost();
        return String.format("{'hostIp':'%s', 'sessionId':'%s'}\n:-)", selectedHost.getHost(), selectedHost.getRadioSession().getId());
    }

    private String assembleApplicationText() {
        return String.format("{'applicationVersion':'%s', 'author':'%s', 'authorEmail':'%s'}", fragment.get().getApplicationVersion(), "", "");
    }


}
