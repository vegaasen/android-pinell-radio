package com.vegaasen.lib.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Simple tools used to help with capturing and requesting status of remote services
 *
 * @author <a href="mailto:vegard.aasen@telenor.com">Vegard Aasen</a>
 * @version 04.08.2015
 * @since 10:40 AM
 */
public final class TelnetUtil {

    private static final Logger LOG = Logger.getLogger(TelnetUtil.class.getSimpleName());

    // todo: The timeout portion may need to be moved around a bit, as it may be too "slow" or "too quick" in regards to Android devices. We'll see..
    private static final long WAIT = TimeUnit.SECONDS.toMillis(8);
    private static final int NUM_OF_HOSTS = 250, NUM_THREADS = 250, NUM_THREADS_ALIVE_ACTIVE = 1;
    private static final int TIMEOUT_DETECT_SUBNET = 20, TIMEOUT_IS_ALIVE = 10, DEFAULT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(2);
    private static final ExecutorService
            EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUM_THREADS),
            ACTIVE_SUBNET = Executors.newFixedThreadPool(NUM_THREADS_ALIVE_ACTIVE),
            ALIVE_HOST_SERVICE = Executors.newFixedThreadPool(NUM_THREADS_ALIVE_ACTIVE);
    private static final String DELIM = "\\.";
    private static final String IP_DELIM = ".";

    private TelnetUtil() {
    }

    public static Set<String> findPotentialLocalSubnetNetworkHosts(int port) {
        try {
            final ActiveSubnetFinder task = new ActiveSubnetFinder();
            final Future<String> potentialSubnets = ACTIVE_SUBNET.submit(task);
            task.getCountDownLatch().await(TIMEOUT_DETECT_SUBNET, TimeUnit.SECONDS);
            final String subnet = potentialSubnets.get();
            return findPotentialLocalSubnetNetworkHosts(subnet, port);
        } catch (Exception e) {
            LOG.warning(String.format("Unable to find potential subnets that was opened on port {%s}", port));
        }
        return Collections.emptySet();
    }

    /**
     * Finds all potential local hosts around in your local network.
     * <p/>
     *
     * @return the lot.
     */
    @SuppressWarnings("unused")
    public static Set<String> findPotentialLocalSubnetNetworkHosts(final String subnet) {
        return findPotentialLocalSubnetNetworkHosts(subnet, 80);
    }

    /**
     * Finds all potential local hosts around in your local network.
     * <p/>
     *
     * @return the lot.
     */
    public static Set<String> findPotentialLocalSubnetNetworkHosts(final String subnet, final int portToVerify) {
        final String splittedSubnet = splitSubnet(subnet);
        final Set<Runnable> runnables = new HashSet<>();
        final Set<String> hosts = new HashSet<>();
        final CountDownLatch potentialSubnetLatch = new CountDownLatch(NUM_OF_HOSTS);
        try (final Selector selector = Selector.open()) {
            for (int i = 0; i <= NUM_OF_HOSTS; i++) {
                final int currPort = i;
                runnables.add(new Runnable() {
                    @Override
                    public void run() {
                        LOG.fine(splittedSubnet + currPort);
                        final String host = splittedSubnet + currPort;
                        if (isAliveWithoutThreading(host, portToVerify, selector)) {
                            hosts.add(host);
                        }
                        potentialSubnetLatch.countDown();
                    }
                });
            }
            if (!runnables.isEmpty()) {
                LOG.info(String.format("Detected {%s} runnable for potential subnet {%s} on local network host using port {%s}", runnables.size(), subnet, portToVerify));
                for (final Runnable runnable : runnables) {
                    EXECUTOR_SERVICE.execute(runnable);
                }
                potentialSubnetLatch.await();
            }
            Set<SelectionKey> selectionKeys = selector.keys();
            if (selectionKeys != null && !selectionKeys.isEmpty()) {
                LOG.fine(String.format("Found {%s} selectionKeys", selectionKeys.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hosts;
    }

    /**
     * Check the aliveness of a portrange.
     * todo: should it be multithreaded?
     *
     * @param hostName  _
     * @param portRange array of portrange. Should be two digits long. E.g {0, 10}
     * @return Map containing the port and the aliveness of that port.
     */
    public static Map<Integer, Boolean> isAlive(final String hostName, final int[] portRange) {
        LOG.info(String.format("Checking host {%s} and portRange {%s}", hostName, Arrays.toString(portRange)));
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

    /**
     * Check the aliveness of the supplied hostname and the port.
     *
     * @param hostName _
     * @param port     _
     * @return aliveness identification
     */
    public synchronized static boolean isAlive(final String hostName, final int port) {
        try {
            final AliveCallable task = new AliveCallable(hostName, port);
            final Future<Boolean> future = ALIVE_HOST_SERVICE.submit(task);
            task.getCountDownLatch().await(TIMEOUT_IS_ALIVE, TimeUnit.SECONDS);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * In cases where you do not want the operation to be threaded, use this
     *
     * @param hostName _
     * @param port     _
     * @return _
     */
    public static boolean isAliveWithoutThreading(final String hostName, final int port, Selector selector) {
        try (SocketChannel socket = SocketChannel.open()) {
            if (!selector.isOpen()) {
                selector.wakeup();
            }
            LOG.info(String.format("Checking {%s} for port opened on {%s}", hostName, port));
            socket.configureBlocking(false);
            socket.connect(new InetSocketAddress(InetAddress.getByName(hostName), port));
            Data data = new Data();
            data.setPort(port);
            data.setStart(System.nanoTime());
            socket.register(selector, SelectionKey.OP_CONNECT, data);
            boolean connected = socket.isConnected();
            LOG.info(String.format("{%s:%s} is connected {%s}", hostName, port, connected));
            return connected;
        } catch (final Exception e) {
            LOG.warning(String.format("%s:%s -- %s", hostName, port, e.getMessage()));
        }
        return false;
    }

    private static void printStatus(final int here, final int there) {
        LOG.fine(String.format("%s %% - %s/%s", ((double) here * 100.0 / (double) there), here, there));
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

    private static final class AliveCallable implements Callable<Boolean> {

        private final CountDownLatch countDownLatch;
        private final String hostName;
        private final int port;

        public AliveCallable(String hostName, int port) {
            this.hostName = hostName;
            this.port = port;
            countDownLatch = new CountDownLatch(NUM_THREADS_ALIVE_ACTIVE);
        }

        @Override
        public Boolean call() throws Exception {
            return detectAliveness();
        }

        private boolean detectAliveness() {
            try (SocketChannel socket = SocketChannel.open()) {
                LOG.info(String.format("Checking {%s} for port opened on {%s}", hostName, port));
                socket.configureBlocking(false);
                socket.connect(new InetSocketAddress(InetAddress.getByName(hostName), port));
                long lastCheck, firstCheck;
                lastCheck = firstCheck = System.currentTimeMillis();
                while (!socket.finishConnect()) {
                    if ((lastCheck - firstCheck) > DEFAULT_TIMEOUT) {
                        LOG.info(String.format("Bind not detected. Cancelling for {%s:%s}", hostName, port));
                        return false;
                    } else {
                        LOG.fine(String.format("Waiting for connection for {%s:%s}", hostName, port));
                        lastCheck = System.currentTimeMillis();
                    }
                }
                return true;
            } catch (final IOException e) {
                // gasp
            } finally {
                countDownLatch.countDown();
            }
            return false;
        }

        public CountDownLatch getCountDownLatch() {
            return countDownLatch;
        }
    }

    private static final class ActiveSubnetFinder implements Callable<String> {

        private final CountDownLatch countDownLatch;

        public ActiveSubnetFinder() {
            countDownLatch = new CountDownLatch(NUM_THREADS_ALIVE_ACTIVE);
        }

        @Override
        public String call() throws Exception {
            return findActiveSubnet();
        }

        private String findActiveSubnet() throws UnknownHostException {
            final String hostAddress = InetAddress.getLocalHost().getHostAddress();
            countDownLatch.countDown();
            LOG.info(String.format("Found potential activeSubnet {%s}", hostAddress));
            return hostAddress;
        }

        public CountDownLatch getCountDownLatch() {
            return countDownLatch;
        }
    }

    private static final class Data {

        private final static int FILTERED = -1;

        private int state = FILTERED;
        private int port;
        private long start;
        private int pass = 0;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setStart(long start) {
            this.start = start;
        }
    }

}