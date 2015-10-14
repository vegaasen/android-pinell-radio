package com.vegaasen.lib.ioc.radio.model.device;

import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;

@Immutable
public class DeviceCurrentlyPlaying {

    private final long lastUpdated;
    private final String name;
    private final String tune;
    private final String graphicsUri;
    private final String rate;
    private final String caps;
    private final String status;
    private final String frequency;
    private final String dabServiceId;
    private final int duration;

    private DeviceCurrentlyPlaying(long lastUpdated, String name, String tune, String graphicsUri, String rate, String caps, String status, String frequency, String dabServiceId, int duration) {
        this.lastUpdated = lastUpdated;
        this.name = name;
        this.tune = tune;
        this.graphicsUri = graphicsUri;
        this.rate = rate;
        this.caps = caps;
        this.status = status;
        this.frequency = frequency;
        this.dabServiceId = dabServiceId;
        this.duration = duration;
    }

    public static DeviceCurrentlyPlaying create(long lastUpdated, String name, String tune, String graphicsUri, String rate, String caps, String status, String frequency, String dabServiceId, int duration) {
        return new DeviceCurrentlyPlaying(lastUpdated, name, tune, graphicsUri, rate, caps, status, frequency, dabServiceId, duration);
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String getName() {
        return name;
    }

    public String getTune() {
        return tune;
    }

    public String getGraphicsUri() {
        return graphicsUri;
    }

    public String getRate() {
        return rate;
    }

    public String getCaps() {
        return caps;
    }

    public String getStatus() {
        return status;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDabServiceId() {
        return dabServiceId;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DeviceCurrentlyPlaying && getName() != null && getName().equals(((DeviceCurrentlyPlaying) obj).getName());
    }

    @Override
    public String toString() {
        return "DeviceCurrentlyPlaying{" +
                "lastUpdated=" + lastUpdated +
                ", name='" + name + '\'' +
                ", tune='" + tune + '\'' +
                ", graphicsUri='" + graphicsUri + '\'' +
                ", rate='" + rate + '\'' +
                ", caps='" + caps + '\'' +
                ", status='" + status + '\'' +
                ", frequency='" + frequency + '\'' +
                ", dabServiceId='" + dabServiceId + '\'' +
                ", duration=" + duration +
                '}';
    }
}
