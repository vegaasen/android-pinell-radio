package com.vegaasen.fun.radio.pinell.listeners;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.async.function.SetPinellHostAsync;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.service.PinellService;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

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
        cancelLoading();
        Log.d(TAG, String.format("CandidateHostBean {%s}", hostBean.toString()));
        final Host candidateHost = pinellService.assembleHost(hostBean);
        Log.d(TAG, String.format("Selecting {%s} as the wanted host, CandidateHost {%s}", position, candidateHost.toString()));
        try {
            new SetPinellHostAsync(pinellService, candidateHost).execute().get(15, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.w(TAG, "Unable to determine pinellHost. Skipping");
        }
        Toast.makeText(getActivity().getBaseContext(), String.format("Connected to %s", candidateHost.getHost()), Toast.LENGTH_SHORT).show();
        hideDialogBoxPostHostSelection();
    }

    private void cancelLoading() {
        if (activity == null) {
            return;
        }
        Log.d(TAG, "Cancelling loading of details regarding pinellHost candidates");
        getActivity().cancel();
    }

    private void hideDialogBoxPostHostSelection() {
        if (activity == null) {
            Log.w(TAG, "Unable to automatically close the activity due to the dialog being nilled");
            return;
        }
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private AbstractActivity getActivity() {
        return activity.get();
    }

}
