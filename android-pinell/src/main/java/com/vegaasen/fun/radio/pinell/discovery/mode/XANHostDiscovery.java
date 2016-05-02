package com.vegaasen.fun.radio.pinell.discovery.mode;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.activity.abs.AbstractActivity;
import com.vegaasen.fun.radio.pinell.discovery.abs.AbstractHostDiscovery;
import com.vegaasen.fun.radio.pinell.discovery.model.HardwareAddress;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.discovery.model.NetInfo;
import com.vegaasen.fun.radio.pinell.discovery.model.RateControl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.DEFAULT_RATECTRL_ENABLE;
import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.DEFAULT_RESOLVE_NAME;
import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.DEFAULT_TIMEOUT_DISCOVER;
import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.KEY_RATECTRL_ENABLE;
import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.KEY_RESOLVE_NAME;
import static com.vegaasen.fun.radio.pinell.common.PinellyConstants.KEY_TIMEOUT_DISCOVER;

/**
 * Finding hosts on the X-AN (WAN, LAN)
 * Todo: Rewrite. All my issues resides in this class.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 1.0
 * @since 11.08.2015
 */
public final class XANHostDiscovery extends AbstractHostDiscovery {

    private final static String TAG = XANHostDiscovery.class.getSimpleName();
    private final static int[] OPTIONS_PORTS = {139, 445, 22, 80};
    private final static int TIMEOUT_SCAN = 1500, TIMEOUT_SHUTDOWN = 10;
    private final static int NUM_THREADS = 8;
    private final static int RATE_ALIVE_HOSTS = 5; // Number of alive hosts between Rate
    private final static ExecutorService POOL = Executors.newFixedThreadPool(NUM_THREADS);

    private int move = 2; // 1=backward 2=forward
    private boolean doRateControl;
    private RateControl mRateControl;

    public XANHostDiscovery(AbstractActivity discover) {
        super(discover);
        mRateControl = new RateControl();
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        final AbstractActivity discover = activity.get();
        if (discover != null) {
            doRateControl = discover.prefs.getBoolean(KEY_RATECTRL_ENABLE, DEFAULT_RATECTRL_ENABLE);
        }
    }

    @Override
    public Void doInBackground(Void... params) {
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
                for (int i = 1; i < sizeHosts; i++) {
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
            POOL.shutdown();
            try {
                if (!POOL.awaitTermination(TIMEOUT_SCAN, TimeUnit.SECONDS)) {
                    POOL.shutdownNow();
                    Log.i(TAG, "Shutting down pool");
                    if (!POOL.awaitTermination(TIMEOUT_SHUTDOWN, TimeUnit.SECONDS)) {
                        Log.w(TAG, "Pool did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                POOL.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    @Override
    public void onCancelled() {
        if (POOL != null) {
            synchronized (POOL) {
                POOL.shutdownNow();
                // FIXME: Prevents some task to end (and close the Save DB)
            }
        }
        super.onCancelled();
    }

    private void launch(long i) {
        if (!POOL.isShutdown()) {
            POOL.execute(new CheckHost(NetInfo.getIpFromLongUnsigned(i)));
        }
    }

    private int getRate() {
        if (doRateControl) {
            return mRateControl.rate;
        }

        final AbstractActivity discover = activity.get();
        if (discover != null) {
            return Integer.parseInt(discover.prefs.getString(KEY_TIMEOUT_DISCOVER, DEFAULT_TIMEOUT_DISCOVER));
        }
        return 1;
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

            // Is gateway ?
            if (discover.net.gatewayIp.equals(host.getIpAddress())) {
                host.setDeviceType(HostBean.TYPE_GATEWAY);
            }

            // FQDN
            // Static
            // DNS
            if (discover.prefs.getBoolean(KEY_RESOLVE_NAME, DEFAULT_RESOLVE_NAME)) {
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
        //see the onProgressUpdate() located in the AbstractHostDiscovery
        publishProgress(host);
    }

    private class CheckHost implements Runnable {

        private String addr;

        private CheckHost(String addr) {
            this.addr = addr;
        }

        @Override
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
                InetAddress candidate = InetAddress.getByName(addr);
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
                if (candidate.isReachable(getRate())) {
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
                if ((port = Reachable.isReachable(candidate, getRate())) > -1) {
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

}
