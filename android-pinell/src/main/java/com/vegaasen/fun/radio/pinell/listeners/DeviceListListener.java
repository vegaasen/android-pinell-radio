package com.vegaasen.fun.radio.pinell.listeners;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.lang.ref.WeakReference;

/**
 * This actually controls the select host activity. Whenever the user selects a wanted host, the activity will be closed with a
 * referenced int representing an "OK" state.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @see com.vegaasen.fun.radio.pinell.activity.host.SelectHostActivity
 */
public class DeviceListListener implements AdapterView.OnItemClickListener {

    private static final String TAG = DeviceListListener.class.getSimpleName();

    private final PinellService pinellService;
    private final WeakReference<AbstractActivity> activity;

    public DeviceListListener(WeakReference<AbstractActivity> activity, PinellService pinellService) {
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
        final HostBean hostBean = getActivity().getHosts().get(position);
        if (hostBean == null) {
            Log.w(TAG, "Unable to fetch wanted hostBean");
            return;
        }
        Log.d(TAG, String.format("CandidateHostBean {%s}", hostBean.toString()));
        final Host candidateHost = pinellService.assembleHost(hostBean);
        Log.d(TAG, String.format("CandidateHost {%s}", candidateHost.toString()));
        final boolean pinellHostSet = pinellService.setCurrentPinellHost(candidateHost);
        Log.d(TAG, String.format("Selecting {%s} as the wanted host. Selection was successful {%s}", position, pinellHostSet));
        Toast.makeText(getActivity().getBaseContext(), String.format("Connected to %s", candidateHost.getHost()), Toast.LENGTH_SHORT).show();
        hideDialogBoxPostHostSelection();
    }

    private void hideDialogBoxPostHostSelection() {
        if (activity == null) {
            Log.w(TAG, "Unable to automatically close the activity due to the dialog being nilled");
            return;
        }
        getActivity().cancel();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private AbstractActivity getActivity() {
        return activity.get();
    }

}
