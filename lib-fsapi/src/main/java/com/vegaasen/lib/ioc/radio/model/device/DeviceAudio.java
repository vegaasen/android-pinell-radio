package com.vegaasen.lib.ioc.radio.model.device;

import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;

@Immutable
public class DeviceAudio {

    private final AudioStatus status;
    private final int level;

    private DeviceAudio(AudioStatus status, int level) {
        this.status = status;
        this.level = level;
    }

    public static DeviceAudio create(AudioStatus status, int level) {
        return new DeviceAudio(status, level);
    }

    public AudioStatus getStatus() {
        return status;
    }

    public int getLevel() {
        return level;
    }
}
