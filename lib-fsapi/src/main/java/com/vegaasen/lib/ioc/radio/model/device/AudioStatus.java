package com.vegaasen.lib.ioc.radio.model.device;

public enum AudioStatus {

    MUTED("1"), UNMUTED("0"), UNKNOWN("-99");

    private final String status;

    private AudioStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static AudioStatus fromValue(final String candidate) {
        if (candidate == null) {
            return AudioStatus.UNKNOWN;
        }
        for (final AudioStatus audioStatus : values()) {
            if (audioStatus.getStatus().equals(candidate)) {
                return audioStatus;
            }
        }
        return AudioStatus.UNKNOWN;
    }

}
