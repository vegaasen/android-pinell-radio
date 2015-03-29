package com.vegaasen.fun.radio.pinell.listner;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * ..what..
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class DeviceListListener implements AdapterView.OnItemClickListener {

    private static final String TAG = DeviceListListener.class.getSimpleName();

    private final PinellService pinellService;
    private final Activity activity;

    public DeviceListListener(Activity activity, PinellService pinellService) {
        this.pinellService = pinellService;
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, String.format("Position clicked {%s} with id {%s} from view {%s} and parent {%s}", position, id, view.getId(), parent.getId()));
        if (position < 0) {
            Log.w(TAG, String.format("Strange position {%s} selected for id {%s}", position, id));
            return;
        }
        if (pinellService == null) {
            Log.e(TAG, "Unable to set the current pinell host due to pinellService being nilled");
            return;
        }
        Log.d(TAG, String.format("Selecting {%s} as the wanted host. Selection was successful {%s}", position, pinellService.setCurrentPinellHost(position)));
        hideDialogBoxPostHostSelection();
    }

    private void hideDialogBoxPostHostSelection() {
        if (activity == null) {
            Log.w(TAG, "Unable to automatically close the activity due to the dialog being nilled");
            return;
        }
        activity.finish();
    }

}
