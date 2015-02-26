package com.vegaasen.lib.ioc.radio.model.device;

import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;

@Immutable
public class DeviceInformation {

    private String name;
    private String version;
    private String apiUrl;

    private DeviceInformation(String name, String version, String apiUrl) {
        this.name = name;
        this.version = version;
        this.apiUrl = apiUrl;
    }

    public static DeviceInformation create(String name, String version, String apiUrl) {
        return new DeviceInformation(name, version, apiUrl);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
