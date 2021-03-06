package com.vegaasen.lib.ioc.radio.model.response;

import com.vegaasen.lib.ioc.radio.model.annotation.Immutable;

import java.util.HashMap;
import java.util.Map;

@Immutable
public class Item {

    // This is the default delimiter - in case of elements of <c8_array/> appears in the value of the k/v..
    public static final String DELIM = ",";

    private static final int UNDEFINED = 999;

    private final int keyId;
    private final Map<String, String> fields;

    private Item(int keyId, Map<String, String> fields) {
        this.keyId = keyId;
        this.fields = fields;
    }

    public static Item create(Map<String, String> fields) {
        return create(UNDEFINED, fields);
    }

    public static Item create(int keyId, Map<String, String> fields) {
        return new Item(keyId, fields);
    }

    public int getKeyId() {
        return keyId;
    }

    public Map<String, String> getFields() {
        return new HashMap<String, String>(fields);
    }
}
