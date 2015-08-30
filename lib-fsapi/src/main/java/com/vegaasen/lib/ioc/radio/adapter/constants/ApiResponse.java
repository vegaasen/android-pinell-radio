package com.vegaasen.lib.ioc.radio.adapter.constants;

public final class ApiResponse {

    public static final String RESPONSE_STATUS = "status";
    public static final String SESSION_SESSION_ID = "sessionId";
    public static final String VALUE = "value";
    public static final String VALUE_U_8 = "u8";
    public static final String VALUE_U_32 = "u32";
    public static final String VALUE_ARRAY_C_8 = "c8_array";
    public static final String FIELD = "field";
    public static final String LABEL = "label";
    public static final String NODE = "node";
    public static final String ITEM = "item";
    public static final String ITEM_NOTIFY = "notify";
    public static final String ITEM_KEY = "key";

    public static final class Device {
        public static final String FRIENDLY_NAME = "friendlyName", VERSION = "version", API_REFERENCE = "webfsapi";
    }

    public static final class RadioMode {
        public static final String ID = "id", NAME = "label", SELECTABLE = "selectable";
    }

    public static final class RadioStation {
        public static final String NAME = "name", TYPE = "type", SUBTYPE = "subtype";
        public static final String MEGAHERTZ = "megahertz";
        public static final String PROPERTY_NAME = "netremote.play.info.name";
    }

    public static final class SubType {
        public static final String TYPE_CONTAINER = "0";
        public static final String TYPE_STATION = "1";
    }

    private ApiResponse() {
    }

}
