package com.vegaasen.fun.radio.pinell.activity.hidden;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.async.function.UpdateAudioLevelAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;

/**
 * Simple activity that is called from the Main activity by clicking some element in the sidebar.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @version 05.01.2016@0.5-SNAPSHOT
 * @see com.vegaasen.fun.radio.pinell.activity.MainActivity
 * @since 05.01.2016@0.5-SNAPSHOT
 */
public class HiddenMenuActivity extends AbstractActivity {

    private ImageButton volumeUp, volumeDown;
    private Button clearHosts, addHost, acquireNewSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_settings);
        configureElements();
        configureActions();
    }

    private void configureElements() {
        volumeUp = (ImageButton) findViewById(R.id.btnSettingsVolumeUp);
        volumeDown = (ImageButton) findViewById(R.id.btnSettingsVolumeDown);
        clearHosts = (Button) findViewById(R.id.btnSettingsClearSavedHosts);
        acquireNewSession = (Button) findViewById(R.id.btnSettingsAquireNewSession);
        addHost = (Button) findViewById(R.id.btnSettingsAddHost);
    }

    private void configureActions() {
        volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApplicationContext.INSTANCE.isRadioConnected()) {
                    new UpdateAudioLevelAsync(getPinellService(), true).execute();
                    Toast.makeText(getBaseContext(), "Volume up", Toast.LENGTH_SHORT).show();
                }
            }
        });
        volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApplicationContext.INSTANCE.isRadioConnected()) {
                    new UpdateAudioLevelAsync(getPinellService(), false).execute();
                    Toast.makeText(getBaseContext(), "Volume down", Toast.LENGTH_SHORT).show();
                }
            }
        });
        clearHosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast
                        .makeText(
                                getBaseContext(),
                                String.format("{%s} hosts cleared from the cache", ApplicationContext.INSTANCE.getStorageService().clear()),
                                Toast.LENGTH_SHORT
                        )
                        .show();
            }
        });
        acquireNewSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
        addHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
