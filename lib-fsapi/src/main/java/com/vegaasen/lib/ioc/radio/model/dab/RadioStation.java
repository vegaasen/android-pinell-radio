package com.vegaasen.lib.ioc.radio.model.dab;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.model.abs.AbsRadioStation;
import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;
import com.vegaasen.lib.ioc.radio.model.response.Item;

@Immutable
public class RadioStation extends AbsRadioStation {

    private RadioStation(int keyId, String name, String type, String subType) {
        super(keyId, name, type, subType);
    }

    public static RadioStation create(Item item) {
        if (!valid(item)) {
            return null;
        }
        return new RadioStation(
                item.getKeyId(),
                item.getFields().get(ApiResponse.RadioStation.NAME),
                item.getFields().get(ApiResponse.RadioStation.TYPE),
                item.getFields().get(ApiResponse.RadioStation.SUBTYPE));
    }

    private static boolean valid(Item item) {
        return !item.getFields().isEmpty() && item.getFields().get(ApiResponse.RadioStation.NAME) != null && item.getFields().get(ApiResponse.RadioStation.TYPE) != null && item.getFields().get(ApiResponse.RadioStation.SUBTYPE) != null;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof RadioStation && getName().equals(((RadioStation) obj).getName());
    }
}
