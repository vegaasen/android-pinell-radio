package com.vegaasen.lib.ioc.radio.model.exception;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public class RadioFsConnectionException extends RuntimeException {

    public RadioFsConnectionException() {
        super("Unable to establish connection to the RadioFS services");
    }

    public RadioFsConnectionException(String message) {
        super(message);
    }
}
