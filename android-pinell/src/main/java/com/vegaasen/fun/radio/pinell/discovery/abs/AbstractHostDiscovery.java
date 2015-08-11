package com.vegaasen.fun.radio.pinell.discovery.abs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;
import com.vegaasen.fun.radio.pinell.R;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.discovery.utils.Prefs;

import java.lang.ref.WeakReference;

/**
 * This is used in order to define the abstract layer related to the network discovery
 * todo: missing implementation of "stop finding hosts"
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 1.0
 * @since 11.8.2015
 */
public abstract class AbstractHostDiscovery extends AsyncTask<Void, HostBean, Void> {

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

    abstract protected Void doInBackground(Void... params);

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
                if (host[0] != null) {
                    discover.addHost(host[0]);
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
            if (discover.prefs.getBoolean(Prefs.KEY_VIBRATE_FINISH, Prefs.DEFAULT_VIBRATE_FINISH)) {
                Vibrator v = (Vibrator) discover.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(AbstractActivity.VIBRATE);
            }
            discover.makeToast(R.string.genericUndocumented);
        }
    }

    @Override
    protected void onCancelled() {
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            discover.makeToast(R.string.genericUndocumented);
        }
        super.onCancelled();
    }

}
