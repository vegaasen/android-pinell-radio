package com.vegaasen.lib.ioc.radio.model.dab;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.model.abs.AbsRadioStation;
import com.vegaasen.lib.ioc.radio.model.response.Item;

public class RadioStation extends AbsRadioStation {

    private RadioStation(int keyId, String name, String type, String subType) {
        super(keyId, name, type, subType);
    }

    public static RadioStation create(Item item) {
        return new RadioStation(
                item.getKeyId(),
                item.getFields().get(ApiResponse.RadioStation.NAME),
                item.getFields().get(ApiResponse.RadioStation.TYPE),
                item.getFields().get(ApiResponse.RadioStation.SUBTYPE));
    }

}
