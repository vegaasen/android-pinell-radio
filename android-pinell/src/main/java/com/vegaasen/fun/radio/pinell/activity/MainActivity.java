package com.vegaasen.fun.radio.pinell.activity;

import android.content.Intent;
import android.os.Bundle;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity;

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
        startActivity(new Intent(this, SelectHostActivity.class));
    }

}
