package com.vegaasen.lib.ioc.radio.model.system;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.model.response.Item;

/**
 * Represents which modes is available through the Radio itself.
 * It might be internetRadio, DAB, AUX etc.
 */
public class RadioMode {

    private static final String SELECTABLE = "1";

    private final int key;
    private final String id;
    private final String name;
    private final boolean selectable;

    private RadioMode(int key, String id, String name, boolean selectable) {
        this.key = key;
        this.id = id;
        this.name = name;
        this.selectable = selectable;
    }

    public static RadioMode create(Item item) {
        if (item != null && !item.getFields().isEmpty()) {
            return new RadioMode(item.getKeyId(),
                    item.getFields().get(ApiResponse.RadioMode.ID),
                    item.getFields().get(ApiResponse.RadioMode.NAME),
                    item.getFields().get(ApiResponse.RadioMode.SELECTABLE).equals(SELECTABLE));
        }
        return null;
    }

    public int getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSelectable() {
        return selectable;
    }
}
