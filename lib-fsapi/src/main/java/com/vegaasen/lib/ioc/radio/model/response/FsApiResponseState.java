package com.vegaasen.lib.ioc.radio.model.response;

/**
 * Represents all (registered) possible return codes from the FsApi :-).
 * Please note that there might be others as well, however I haven"t found those yet.
 *
 * @author vegaasen
 */
public enum FsApiResponseState {

    OK("FS_OK"),
    FAIL("FS_FAIL"),
    BAD_PACKET("FS_PACKET_BAD"),
    NOT_EXIST("FS_NODE_DOES_NOT_EXIST"),
    BLOCKED("FS_NODE_BLOCKED"),
    TIMEOUT("FS_TIMEOUT"),
    LIST_END("FS_LIST_END"),
    UNKNOWN("----");

    private final String status;

    private FsApiResponseState(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static FsApiResponseState fromStatus(final String candidate) {
        if (candidate == null || candidate.isEmpty()) {
            return UNKNOWN;
        }
        for (final FsApiResponseState state : values()) {
            if (state.getStatus().equals(candidate)) {
                return state;
            }
        }
        return UNKNOWN;
    }

}
