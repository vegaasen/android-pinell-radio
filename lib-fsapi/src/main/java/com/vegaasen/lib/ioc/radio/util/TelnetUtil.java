package com.vegaasen.lib.ioc.radio.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Simple tools used to help with capturing and requesting status of remote services
 *
 * @author <a href="mailto:vegard.aasen@telenor.com">Vegard Aasen</a>
 * @since 10:40 AM
 */
public final class TelnetUtil {

    // The timeout portion may need to be moved around a bit, as it may be too "slow" or "too quick" in regards to Android devices. We'll see..
    private static final int TIMEOUT = 1500;
    private static final long WAIT = TimeUnit.SECONDS.toMillis(5);
    private static final int NUM_OF_HOSTS = 250, NUM_THREADS = 30;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUM_THREADS), ACTIVE_SUBNET = Executors.newFixedThreadPool(1);
    private static final CountDownLatch LATCH = new CountDownLatch(NUM_OF_HOSTS), ACTIVE_SUBNET_LATCH = new CountDownLatch(1);
    private static final String DELIM = "\\.";
    private static final String IP_DELIM = ".";

    private TelnetUtil() {
    }

    public static Set<String> findPotentialLocalSubnetNetworkHosts() {
        try {
            final Future<String> future = ACTIVE_SUBNET.submit(new ActiveSubnetFinder());
            ACTIVE_SUBNET_LATCH.await(20, TimeUnit.SECONDS);
            final String subnet = future.get();
            return findPotentialLocalSubnetNetworkHosts(subnet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    /**
     * Finds all potential local hosts around in your local network.
     * <p/>
     *
     * @return the lot.
     */
    public static Set<String> findPotentialLocalSubnetNetworkHosts(final String subnet) {
        final String splittedSubnet = splitSubnet(subnet);
        final Set<Runnable> runnables = new HashSet<>();
        final Set<String> hosts = new HashSet<>();
        try {
            for (int i = 0; i <= NUM_OF_HOSTS; i++) {
                final int currPort = i;
                runnables.add(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(splittedSubnet + currPort);
                        final String host = splittedSubnet + currPort;
                        if (isAlive(host, 80)) {
                            hosts.add(host);
                        }
                        LATCH.countDown();
                    }
                });
            }
        } catch (Exception e) {
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
        final Map<Integer, Boolean> alives = new HashMap<>();
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

    //todo: must introduce a CDL / Callable here as well due to Android limitations. MEH!
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
            // gasp
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

    private static String splitSubnet(final String subnet) {
        String reformattedSubnet = "";
        int c = 0;
        for (final String part : subnet.split(DELIM)) {
            reformattedSubnet += part + IP_DELIM;
            if (c++ == 2) {
                break;
            }
        }
        return reformattedSubnet;
    }

    private static final class ActiveSubnetFinder implements Callable<String> {
        @Override
        public String call() throws Exception {
            return findActiveSubnet();
        }

        private String findActiveSubnet() throws UnknownHostException {
            final String subnet = InetAddress.getLocalHost().getHostAddress();
            ACTIVE_SUBNET_LATCH.countDown();
            return subnet;
        }
    }

}