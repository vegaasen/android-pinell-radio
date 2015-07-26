package com.vegaasen.http.rest.model.http;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public enum ContentType {

    WHATEVER("*/*"),
    APPLICATION_JSON("application/json"),
    APPLICATION_ATOM_XML("application/atom+xml"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_XML("application/xml"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml");

    private final String variant;

    private ContentType(final String variant) {
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }
}
