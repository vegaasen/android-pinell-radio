package com.vegaasen.lib.ioc.radio.model.system;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;
import com.vegaasen.lib.ioc.radio.model.response.Item;

/**
 * Represents which modes is available through the Radio itself.
 * It might be internetRadio, DAB, AUX etc.
 */
@Immutable
public class RadioMode {

    private static final String SELECTABLE = "1";
    private static final String EMPTY = "";

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
        if (item != null) {
            return new RadioMode(item.getKeyId(),
                    item.getFields().isEmpty() ? EMPTY : item.getFields().get(ApiResponse.RadioMode.ID),
                    item.getFields().isEmpty() ? EMPTY : item.getFields().get(ApiResponse.RadioMode.NAME),
                    !item.getFields().isEmpty() && item.getFields().get(ApiResponse.RadioMode.SELECTABLE).equals(SELECTABLE));
        }
        return null;
    }

    public int getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getKeyAsString() {
        return Integer.toString(key);
    }

    public String getName() {
        return name;
    }

    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof RadioMode) {
            RadioMode radioMode = (RadioMode) obj;
            return getKey() == radioMode.getKey();
        }
        return false;
    }

    @Override
    public String toString() {
        return "RadioMode{" +
                "key=" + key +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", selectable=" + selectable +
                '}';
    }
}
