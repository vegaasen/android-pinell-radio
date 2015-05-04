package com.vegaasen.lib.ioc.radio.model.system.connection;

import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.system.auth.RadioSession;

public class Host {

    private static final String HTTP_SCHEME = "http";

    private final String host;
    private final int port;
    private final String code;
    private RadioSession radioSession;
    private DeviceInformation deviceInformation;

    private Host(String host, int port, String code) {
        this.host = host;
        this.port = port;
        this.code = code;
    }

    public static Host create(String host, int port, String code) {
        return new Host(host, port, code);
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
    public String toString() {
        return "Host{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", code='" + code + '\'' +
                '}';
    }
}
