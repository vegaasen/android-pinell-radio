package com.vegaasen.fun.radio.pinell.common;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public final class Constants {

    public static final String PACKAGE = "com.vegaasen.fun.radio.pinell";

    public static final String KEY_RESOLVE_NAME = "resolve_name";
    public static final boolean DEFAULT_RESOLVE_NAME = true;
    public static final String KEY_VIBRATE_FINISH = "vibrate_finish";
    public static final boolean DEFAULT_VIBRATE_FINISH = false;
    public static final String KEY_PORT_START = "port_start";
    public static final String DEFAULT_PORT_START = "1";
    public static final String KEY_PORT_END = "port_end";
    public static final String DEFAULT_PORT_END = "1024";
    public static final int MAX_PORT_END = 65535;
    public static final String KEY_SSH_USER = "ssh_user";
    public static final String DEFAULT_SSH_USER = "root";
    public static final String KEY_NTHREADS = "nthreads";
    public static final String DEFAULT_NTHREADS = "8";
    public static final String KEY_RESET_NICDB = "resetdb";
    public static final int DEFAULT_RESET_NICDB = 1;
    public static final String KEY_RESET_SERVICESDB = "resetservicesdb";
    public static final int DEFAULT_RESET_SERVICESDB = 1;
    public static final String KEY_METHOD_DISCOVER = "discovery_method";
    public static final String DEFAULT_METHOD_DISCOVER = "0";
    public static final String KEY_METHOD_PORTSCAN = "method_portscan";
    public static final String DEFAULT_METHOD_PORTSCAN = "0";
    public static final String KEY_TIMEOUT_FORCE = "timeout_force";
    public static final boolean DEFAULT_TIMEOUT_FORCE = false;
    public static final String KEY_TIMEOUT_PORTSCAN = "timeout_portscan";
    public static final String DEFAULT_TIMEOUT_PORTSCAN = "500";
    public static final String KEY_RATECTRL_ENABLE = "ratecontrol_enable";
    public static final boolean DEFAULT_RATECTRL_ENABLE = true;
    public static final String KEY_TIMEOUT_DISCOVER = "timeout_discover";
    public static final String DEFAULT_TIMEOUT_DISCOVER = "500";
    public static final String KEY_BANNER = "banner";
    public static final boolean DEFAULT_BANNER = true;
    public static final String KEY_MOBILE = "allow_mobile";
    public static final boolean DEFAULT_MOBILE = false;
    public static final String KEY_INTF = "interface";
    public static final String DEFAULT_INTF = null;
    public static final String KEY_IP_START = "ip_start";
    public static final String DEFAULT_IP_START = "0.0.0.0";
    public static final String KEY_IP_END = "ip_end";
    public static final String DEFAULT_IP_END = "0.0.0.0";
    public static final String KEY_IP_CUSTOM = "ip_custom";
    public static final boolean DEFAULT_IP_CUSTOM = false;
    public static final String KEY_CIDR_CUSTOM = "cidr_custom";
    public static final boolean DEFAULT_CIDR_CUSTOM = false;
    public static final String KEY_CIDR = "cidr";
    public static final String DEFAULT_CIDR = "24";
    public static final String KEY_DONATE = "donate";
    public static final String KEY_WEBSITE = "website";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_VERSION = "version";
    public static final String KEY_WIFI = "wifi";

    private Constants() {
    }
}
