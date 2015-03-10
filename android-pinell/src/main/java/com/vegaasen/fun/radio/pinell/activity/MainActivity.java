package com.vegaasen.fun.radio.pinell.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.vegaasen.fun.radio.pinell.R;

/**
 * Simple buttons
 */
public class MainActivity extends Activity {

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
                    final Dialog dialog = new Dialog(getBaseContext());
                    dialog.setContentView(R.layout.dialog_device_chooser);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();
                }
            });
        }
    }

}
