package com.vegaasen.lib.ioc.radio.model.system.connection;

import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.auth.RadioSession;

import java.util.Objects;

/**
 * @author vegardaasen
 * @version 05.1.2016@0.5
 * @since 29.1.2015@0.1
 */
public class Host {

    private static final String HTTP_SCHEME = "http";

    private final String host;
    private final int port;
    private final String code;

    private RadioSession radioSession;
    private DeviceInformation deviceInformation;
    private boolean cached;

    private Host(String host, int port, String code) {
        this.host = host;
        this.port = port;
        this.code = code;
    }

    public static Host create(String host, int port, String code) {
        return new Host(host, port, code);
    }

    public static Host create(String friendlyName, String host, int port, String code) {
        Host candidate = create(host, port, code);
        candidate.setDeviceInformation(DeviceInformation.create(friendlyName, null, null));
        return candidate;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getCode() {
        return code;
    }

    public RadioSession getRadioSession() {
        return radioSession;
    }

    public void setRadioSession(final RadioSession session) {
        this.radioSession = session;
    }

    public DeviceInformation getDeviceInformation() {
        return deviceInformation;
    }

    public void setDeviceInformation(DeviceInformation deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public void removeRadioSession() {
        this.radioSession = null;
    }

    public String getConnectionString() {
        return String.format("%s://%s:%s", HTTP_SCHEME, getHost(), getPort());
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, code, radioSession, deviceInformation, cached);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Host other = (Host) obj;
        return Objects.equals(this.host, other.host)
                && Objects.equals(this.port, other.port)
                && Objects.equals(this.code, other.code)
                && Objects.equals(this.radioSession, other.radioSession)
                && Objects.equals(this.deviceInformation, other.deviceInformation)
                && Objects.equals(this.cached, other.cached);
    }

    @Override
    public String toString() {
        return "Host{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", code='" + code + '\'' +
                '}';
    }
}
