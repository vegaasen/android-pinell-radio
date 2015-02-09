package com.vegaasen.lib.ioc.radio.model.system;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.model.response.Item;

public class Equalizer {

    private static final String UNKNOWN = "";

    private final int key;
    private final String name;

    private Equalizer(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public static Equalizer create(Item item) {
        if (item != null) {
            return new Equalizer(
                    item.getKeyId(),
                    item.getFields().isEmpty() ? UNKNOWN : item.getFields().get(ApiResponse.LABEL));
        }
        return null;
    }

    public int getKey() {
        return key;
    }

    public String getKeyAsString() {
        return Integer.toString(key);
    }

    public String getName() {
        return name;
    }
}
