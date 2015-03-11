package com.vegaasen.fun.radio.pinell.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;

/**
 * Main activity which simply controls the "entrance" to the application.
 * It will also display some stuff regarding the various contexts the user selects.
 *
 * @author vegaasen
 * @since 0.1-SNAPSHOT
 */
public class MainActivity extends AbstractActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        configureButtons();
    }

    private void configureButtons() {
        Button button = (Button) findViewById(R.id.btnDialog);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    generateDeviceConnectionDialog();
                }
            });
        }
    }

}
