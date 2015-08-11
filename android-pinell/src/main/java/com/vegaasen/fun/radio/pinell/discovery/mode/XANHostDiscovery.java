package com.vegaasen.fun.radio.pinell.discovery.mode;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.discovery.abs.AbstractHostDiscovery;
import com.vegaasen.fun.radio.pinell.discovery.model.HardwareAddress;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.discovery.model.NetInfo;
import com.vegaasen.fun.radio.pinell.discovery.model.RateControl;
import com.vegaasen.fun.radio.pinell.discovery.utils.Prefs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Finding hosts on the X-AN (WAN, LAN)
 * Todo: Beautify
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 1.0
 * @since 11.08.2015
 */
public final class XANHostDiscovery extends AbstractHostDiscovery {

    private static final String TAG = XANHostDiscovery.class.getSimpleName();
    private static final int[] OPTIONS_PORTS = {139, 445, 22, 80};
    private static final int TIMEOUT_SCAN = 3600, TIMEOUT_SHUTDOWN = 10;
    private static final int NUM_THREADS = 10; //FIXME: Test, plz set in options again ?
    private static final int RATE_ALIVE_HOSTS = 5; // Number of alive hosts between Rate

    private final ExecutorService mPool = Executors.newFixedThreadPool(NUM_THREADS);

    private int move = 2; // 1=backward 2=forward
    private boolean doRateControl;
    private RateControl mRateControl;

    public XANHostDiscovery(AbstractActivity discover) {
        super(discover);
        mRateControl = new RateControl();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            doRateControl = discover.prefs.getBoolean(Prefs.KEY_RATECTRL_ENABLE,
                    Prefs.DEFAULT_RATECTRL_ENABLE);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            Log.d(TAG, "start=" + NetInfo.getIpFromLongUnsigned(start) + " (" + start
                    + "), end=" + NetInfo.getIpFromLongUnsigned(end) + " (" + end
                    + "), length=" + size);
            if (ip <= end && ip >= start) {
                Log.d(TAG, "Back and forth scanning");
                // gateway
                launch(start);
                // hosts
                long ptBackward = ip, ptForward = ip + 1, sizeHosts = size - 1;
                for (int i = 0; i < sizeHosts; i++) {
                    // Set pointer if of limits
                    if (ptBackward <= start) {
                        move = 2;
                    } else if (ptForward > end) {
                        move = 1;
                    }
                    // Move back and forth
                    if (move == 1) {
                        launch(ptBackward);
                        ptBackward--;
                        move = 2;
                    } else if (move == 2) {
                        launch(ptForward);
                        ptForward++;
                        move = 1;
                    }
                }
            } else {
                for (long i = start; i <= end; i++) {
                    launch(i);
                }
            }
            mPool.shutdown();
            try {
                if (!mPool.awaitTermination(TIMEOUT_SCAN, TimeUnit.SECONDS)) {
                    mPool.shutdownNow();
                    Log.i(TAG, "Shutting down pool");
                    if (!mPool.awaitTermination(TIMEOUT_SHUTDOWN, TimeUnit.SECONDS)) {
                        Log.w(TAG, "Pool did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                mPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        if (mPool != null) {
            synchronized (mPool) {
                mPool.shutdownNow();
                // FIXME: Prevents some task to end (and close the Save DB)
            }
        }
        super.onCancelled();
    }

    private void launch(long i) {
        if (!mPool.isShutdown()) {
            mPool.execute(new CheckRunnable(NetInfo.getIpFromLongUnsigned(i)));
        }
    }

    private int getRate() {
        if (doRateControl) {
            return mRateControl.rate;
        }

        final AbstractActivity discover = activity.get();
        if (discover != null) {
            return Integer.parseInt(discover.prefs.getString(Prefs.KEY_TIMEOUT_DISCOVER,
                    Prefs.DEFAULT_TIMEOUT_DISCOVER));
        }
        return 1;
    }

    private class CheckRunnable implements Runnable {
        private String addr;

        CheckRunnable(String addr) {
            this.addr = addr;
        }

        public void run() {
            if (isCancelled()) {
                publish(null);
            }
            Log.d(TAG, String.format("Checking {%s}", addr));
            // Create host object
            final HostBean host = new HostBean();
            host.setResponseTime(getRate());
            host.setIpAddress(addr);
            try {
                InetAddress h = InetAddress.getByName(addr);
                // Rate control check
                if (doRateControl && mRateControl.indicator != null && hosts_done % RATE_ALIVE_HOSTS == 0) {
                    mRateControl.adaptRate();
                }
                // Arp Check #1
                host.setHardwareAddress(HardwareAddress.getHardwareAddress(addr));
                if (!NetInfo.NOMAC.equals(host.getHardwareAddress())) {
                    Log.e(TAG, "found using arp #1 " + addr);
                    publish(host);
                    return;
                }
                // Native InetAddress check
                if (h.isReachable(getRate())) {
                    Log.e(TAG, "found using InetAddress ping " + addr);
                    publish(host);
                    // Set indicator and get a rate
                    if (doRateControl && mRateControl.indicator == null) {
                        mRateControl.indicator = addr;
                        mRateControl.adaptRate();
                    }
                    return;
                }
                // Arp Check #2
                host.setHardwareAddress(HardwareAddress.getHardwareAddress(addr));
                if (!NetInfo.NOMAC.equals(host.getHardwareAddress())) {
                    Log.e(TAG, "found using arp #2 " + addr);
                    publish(host);
                    return;
                }
                // Custom check
                int port;
                // TODO: Get ports from options
                for (int optionsPort : OPTIONS_PORTS) {
                    Socket socket = new Socket();
                    try {
                        socket.bind(null);
                        socket.connect(new InetSocketAddress(addr, optionsPort), getRate());
                        Log.v(TAG, "found using TCP connect " + addr + " on port=" + optionsPort);
                    } catch (IOException | IllegalArgumentException e) {
                        //nvm
                    } finally {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            //nvm
                        }
                    }
                }
                /*
                if ((port = Reachable.isReachable(h, getRate())) > -1) {
                    Log.v(TAG, "used Network.Reachable object, "+addr+" port=" + port);
                    publish(host);
                    return;
                }
                */
                // Arp Check #3
                host.setHardwareAddress(HardwareAddress.getHardwareAddress(addr));
                if (!NetInfo.NOMAC.equals(host.getHardwareAddress())) {
                    Log.e(TAG, "found using arp #3 " + addr);
                    publish(host);
                    return;
                }
                publish(null);

            } catch (IOException e) {
                publish(null);
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void publish(final HostBean host) {
        hosts_done++;
        if (host == null) {
            publishProgress((HostBean) null);
            return;
        }
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            // Mac Addr not already detected
            if (NetInfo.NOMAC.equals(host.getHardwareAddress())) {
                host.setHardwareAddress(HardwareAddress.getHardwareAddress(host.getIpAddress()));
            }

            // NIC vendor
            host.setNicVendor(HardwareAddress.getNicVendor(host.getHardwareAddress()));

            // Is gateway ?
            if (discover.net.gatewayIp.equals(host.getIpAddress())) {
                host.setDeviceType(HostBean.TYPE_GATEWAY);
            }

            // FQDN
            // Static
            // DNS
            if (discover.prefs.getBoolean(Prefs.KEY_RESOLVE_NAME, Prefs.DEFAULT_RESOLVE_NAME)) {
                try {
                    host.setHostname((InetAddress.getByName(host.getIpAddress())).getCanonicalHostName());
                } catch (UnknownHostException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            // TODO: NETBIOS
            //try {
            //    host.hostname = NbtAddress.getByName(addr).getHostName();
            //} catch (UnknownHostException e) {
            //    Log.i(TAG, e.getMessage());
            //}
        }
        publishProgress(host);
    }

}
