package com.vegaasen.lib.ioc.radio.adapter.constants;

public final class UriContext {

    public static final String ROOT = "/fsapi", SLASH = "/";

    public static final class Authentication {
        public static final String CREATE_SESSION = SLASH + "CREATE_SESSION";
    }

    public static final class System {
        public static final String GET_POWER_STATE = SLASH + "GET/netRemote.sys.power";
        public static final String SET_POWER_STATE = SLASH + "SET/netRemote.sys.power";
        public static final String VALID_MODES = SLASH + "LIST_GET_NEXT/netRemote.sys.caps.validModes" + SLASH + "%s";
        public static final String VALID_EQUALIZERS = SLASH + "LIST_GET_NEXT/netRemote.sys.caps.eqPresets" + SLASH + "%s";
        public static final String GET_EQUALIZER = SLASH + "GET/netRemote.sys.audio.eqPreset";
        public static final String SET_EQUALIZER = SLASH + "SET/netRemote.sys.audio.eqPreset";
    }

    public static final class Device {
        public static final String INFORMATION = SLASH + "device";
        public static final String CURRENTLY_PLAYING_RATE = SLASH + "GET/netRemote.play.rate";
        public static final String CURRENTLY_PLAYING_CAPS = SLASH + "GET/netRemote.play.caps";
        public static final String CURRENTLY_PLAYING_NAME = SLASH + "GET/netRemote.play.info.name";
        public static final String CURRENTLY_PLAYING_TEXT = SLASH + "GET/netRemote.play.info.text";
        public static final String CURRENTLY_PLAYING_STATUS = SLASH + "GET/netRemote.play.status";
        public static final String CURRENTLY_PLAYING_FREQUENCY = SLASH + "GET/netRemote.play.frequency";
        public static final String CURRENTLY_PLAYING_DURATION = SLASH + "GET/netRemote.play.info.duration";
        public static final String CURRENTLY_PLAYING_GRAPHICAL = SLASH + "GET/netRemote.play.info.graphicUri";
        public static final String CURRENTLY_PLAYING_DAB_SERVICE_ID = SLASH + "GET/netRemote.play.serviceIds.dabServiceId";
        public static final String AUDIO_VOLUME_LEVEL = SLASH + "GET/netRemote.sys.audio.volume";
        public static final String AUDIO_VOLUME_MUTED = SLASH + "GET/netRemote.sys.audio.mute";
    }

    public static final class RadioNavigation {
        //this require that there is some kind of "init" param. Please use String.format(string, param)
        public static final String STATION_LIST = SLASH + "LIST_GET_NEXT/netRemote.nav.list" + SLASH + "%s";
        public static final String PRE_GET_NAV_CAPS = SLASH + "GET/netRemote.nav.caps";
        public static final String PRE_GET_NAV_STATE = SLASH + "GET/netRemote.nav.state";
        public static final String PRE_SET_NAV_STATE = SLASH + "GET/netRemote.nav.state";
        public static final String PRE_GET_NAV_STATUS = SLASH + "GET/netRemote.nav.status";
        public static final String SELECT_SUB_CONTAINER = SLASH + "SET/netRemote.nav.action.navigate";
    }

    private UriContext() {
    }

}
