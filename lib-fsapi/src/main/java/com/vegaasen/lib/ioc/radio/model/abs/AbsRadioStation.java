package com.vegaasen.lib.ioc.radio.model.abs;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;

@Immutable
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

    /**
     * The radioStation-candidate may also be an container. This verifies that it is not.
     * @return _
     */
    public boolean isRadioStation() {
        return subType!=null && !subType.isEmpty() && subType.equals(ApiResponse.SubType.TYPE_STATION);
    }

    @Override
    public String toString() {
        return "AbsRadioStation{" +
                "keyId=" + keyId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                '}';
    }
}
