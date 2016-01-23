package com.vegaasen.fun.radio.pinell.discovery.abs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import com.google.common.collect.Lists;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.async.function.host.UpdateHostBeanInformationAsync;
import com.vegaasen.fun.radio.pinell.context.ApplicationContext;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.lib.ioc.radio.adapter.fsapi.ApiConnection;
import com.vegaasen.lib.utils.TelnetUtil;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.DEFAULT_VIBRATE_FINISH;
import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.KEY_VIBRATE_FINISH;

/**
 * This is used in order to define the abstract layer related to the network discovery
 * todo: missing implementation of "stop finding hosts"
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 1.0
 * @since 11.8.2015
 */
public abstract class AbstractHostDiscovery extends AsyncTask<Void, HostBean, Void> {

    private static final String TAG = AbstractHostDiscovery.class.getSimpleName();
    private static final List<Integer> PORTS = Lists.newArrayList(ApiConnection.DEFAULT_FS_PORT, ApiConnection.ALTERNATIVE_FS_PORT);

    protected final WeakReference<AbstractActivity> activity;

    protected int hosts_done = 0;
    protected long ip;
    protected long start = 0;
    protected long end = 0;
    protected long size = 0;

    public AbstractHostDiscovery(AbstractActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public void setNetwork(long ip, long start, long end) {
        this.ip = ip;
        this.start = start;
        this.end = end;
    }

    @Override
    protected abstract Void doInBackground(Void... params);

    public void hardCancel() {
        onCancelled();
    }

    @Override
    protected void onPreExecute() {
        size = (int) (end - start + 1);
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            discover.setProgress(0);
        }
    }

    @Override
    protected void onProgressUpdate(HostBean... host) {
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            if (!isCancelled()) {
                final HostBean candidate = host[0];
                if (candidate != null) {
                    Log.i(TAG, String.format("Verifying candidate for aliveness {%s}", candidate.getIpAddress()));
                    final Set<Integer> candidates = TelnetUtil.isAlive(candidate.getIpAddress(), PORTS);
                    if (!candidates.isEmpty()) {
                        candidate.setPortsOpen(candidates);
                        new UpdateHostBeanInformationAsync(ApplicationContext.INSTANCE.getPinellService(), candidate).silentGet();
                        discover.addHost(candidate);
                    }
                }
                if (size > 0) {
                    discover.setProgress((int) (hosts_done * 10000 / size));
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Void unused) {
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            if (discover.prefs.getBoolean(KEY_VIBRATE_FINISH, DEFAULT_VIBRATE_FINISH)) {
                Vibrator v = (Vibrator) discover.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(AbstractActivity.VIBRATE);
            }
            discover.makeToast(R.string.discoveryComplete);
            if (discover.getHosts().isEmpty()) {
                discover.postLoadingActions();
            }
        }
    }

    @Override
    protected void onCancelled() {
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            Log.i(TAG, "Scanning cancelled");
        }
        super.onCancelled();
    }

}
