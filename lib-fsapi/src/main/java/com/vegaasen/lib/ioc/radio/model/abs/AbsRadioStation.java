package com.vegaasen.lib.ioc.radio.model.abs;

public abstract class AbsRadioStation {

    private final int keyId;
    private final String name;
    private final String type;
    private final String subType;

    public AbsRadioStation(int keyId, String name, String type, String subType) {
        this.keyId = keyId;
        this.name = name;
        this.type = type;
        this.subType = subType;
    }

    public int getKeyId() {
        return keyId;
    }

    public String getKeyIdAsString() {
        return Integer.toString(keyId);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }
}
