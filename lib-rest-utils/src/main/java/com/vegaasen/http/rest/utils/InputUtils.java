package com.vegaasen.http.rest.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 24.1.2016
 */
public class InputUtils {

    private static final Logger LOG = Logger.getLogger(InputUtils.class.getSimpleName());
    private static final String EMPTY = "";

    private InputUtils() {
    }

    public static String convertInputStreamToPayload(final InputStream stream) {
        if (stream == null) {
            return EMPTY;
        }
        final InputStreamReader reader = new InputStreamReader(stream);
        final StringBuilder builder = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String read;
            read = bufferedReader.readLine();
            while (read != null) {
                builder.append(read);
                read = bufferedReader.readLine();

            }
        } catch (final IOException e) {
            LOG.warning("Unable to convert stream->payload/string");
        }
        return builder.toString();
    }

}
