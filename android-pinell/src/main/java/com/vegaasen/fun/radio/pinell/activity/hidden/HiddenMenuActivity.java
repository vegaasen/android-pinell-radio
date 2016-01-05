package com.vegaasen.fun.radio.pinell.activity.hidden;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    }

    private void configureActions() {
        volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApplicationContext.INSTANCE.isRadioConnected()) {
                    new UpdateAudioLevelAsync(getPinellService(), true).execute();
                }
            }
        });
        volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApplicationContext.INSTANCE.isRadioConnected()) {
                    new UpdateAudioLevelAsync(getPinellService(), false).execute();
                }
            }
        });
    }

}
