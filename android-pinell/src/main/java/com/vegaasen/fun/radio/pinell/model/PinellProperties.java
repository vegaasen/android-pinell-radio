package com.vegaasen.fun.radio.pinell.model;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @since 04.05.2015
 */
public enum PinellProperties {

    CODE("code"), CONNECTION_STRING("connectionString"), HOST("host"), PORT("port"),
    RADIO_SESSION("session"),
    DEVICE_NAME("deviceName"), DEVICE_VERSION("deviceVersion"), DEVICE_API("deviceApi");

    private final String key;

    PinellProperties(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
