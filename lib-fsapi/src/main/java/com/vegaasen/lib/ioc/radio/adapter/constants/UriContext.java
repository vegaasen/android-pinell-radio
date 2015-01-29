package com.vegaasen.lib.ioc.radio.adapter.constants;

public final class UriContext {

    public static final String ROOT = "/fsapi", SLASH = "/";

    public static final class Authentication {

        public static final String CREATE_SESSION = SLASH + "CREATE_SESSION";

    }

    public static final class System {

        public static final String POWER_STATE_CURRENT = SLASH + "GET/netRemote.sys.power";
        public static final String POWER_STATE_SET = SLASH + "SET/netRemote.sys.power";

    }

    private UriContext() {
    }

}
