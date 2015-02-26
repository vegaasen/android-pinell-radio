package com.vegaasen.lib.ioc.radio.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Simple tools used to help with capturing and requesting status of remote services
 *
 * @author <a href="mailto:vegard.aasen@telenor.com">Vegard Aasen</a>
 * @since 10:40 AM
 */
public final class TelnetUtil {

    // The timeout portion may need to be moved around a bit, as it may be too "slow" or "too quick" in regards to Android devices. We'll see..
    private static final int TIMEOUT = 500;
    private static final long WAIT = TimeUnit.SECONDS.toMillis(5);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(15);
    private static final CountDownLatch LATCH = new CountDownLatch(250);

    private TelnetUtil() {
    }

    /**
     * Finds all potential local hosts around in your local network.
     * <p/>
     * Todo: need multi-threading to speed things up a bit
     * Todo: need to start at a higher range than right now (!=1..)?
     *
     * @return the lot.
     */
    public static Set<String> findPotentialLocalSubnetNetworkHosts() {
        final Set<Runnable> runnables = new HashSet<Runnable>();
        final Set<String> hosts = new HashSet<String>();
        try {
            final String subnet = findActiveSubnet();
            for (int i = 0; i <= 250; i++) {
                final int currPort = i;
                runnables.add(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(currPort);
                        final String host = subnet + currPort;
                        if (isAlive(host, 80)) {
                            hosts.add(host);
                        }
                        LATCH.countDown();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!runnables.isEmpty()) {

            for (final Runnable runnable : runnables) {
                EXECUTOR_SERVICE.execute(runnable);
            }
            try {
                LATCH.await();
            } catch (InterruptedException e) {
                // *gulp*
            }
        }
        return hosts;
    }

    public static Map<Integer, Boolean> isAlive(final String hostName, final int[] portRange) {
        if (portRange.length != 2) {
            throw new RuntimeException("Ops. too many port in the portRange. Expected 2, found " + portRange.length);
        }
        final Map<Integer, Boolean> alives = new HashMap<Integer, Boolean>();
        long lastWait = 0L;
        for (int i = portRange[0]; i <= portRange[1]; i++) {
            alives.put(i, isAlive(hostName, i));
            if (lastWait == 0 || System.currentTimeMillis() > lastWait + WAIT) {
                printStatus(i, portRange[1]);
                lastWait = System.currentTimeMillis();
            }
        }
        return alives;
    }

    public static boolean isAlive(final String hostName, final int port) {
        final Socket pingSocket = new Socket();
        try {
            pingSocket.setSoTimeout(TIMEOUT);
            pingSocket.connect(new InetSocketAddress(hostName, port), TIMEOUT);
            pingSocket.isBound();
            pingSocket.setKeepAlive(false);
            if (pingSocket.isConnected()) {
                return true;
            }
        } catch (final IOException e) {
            //
        } finally {
            try {
                pingSocket.close();
            } catch (IOException e) {
                //jeje
            }
        }
        return false;
    }

    private static void printStatus(final int here, final int there) {
        System.out.println(String.format("%s %% - %s/%s", ((double) here * 100.0 / (double) there), here, there));
    }

    private static String findActiveSubnet() throws UnknownHostException {
        String subnet = "";
        int c = 0;
        for (final String part : InetAddress.getLocalHost().getHostAddress().split("\\.")) {
            subnet += part + ".";
            if (c++ == 2) {
                break;
            }
        }
        return subnet;
    }

}