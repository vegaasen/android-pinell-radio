package com.vegaasen.fun.radio.pinell.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity;

/**
 * This is the main activity which controls all the various fragments within the application itself.
 * It also (by default) initalizes the "SelectHostActivity" in the startup of the application.
 *
 * @author vegaasen
 * @since 0.1-SNAPSHOT
 */
public class MainActivity extends AbstractActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Intent intent = new Intent(this, SelectHostActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, String.format("Response from request {%s} was {%s}", requestCode, resultCode));
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "Woppey!");
        }
    }
}
