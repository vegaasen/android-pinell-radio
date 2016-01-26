package com.vegaasen.lib.ioc.radio.util;

/**
 * Utilities that helps with the various elements/items that is exposed from the FS API.
 * E.g:
 * - Formatting
 * - Trimming
 * - etc..
 * todo: trimming seems not to work anymore. wtf.
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 * @version 26.7.2015
 * @since 26.7.2015
 */
public final class ItemUtils {

    private static final String EMPTY = "", SPACE = "(\\s)[2,_]", TAB = "\\t", QUESTION_MARK = "\\?", REPLACEMENT = " ";
    private static final int DEFAULT = -1;

    private ItemUtils() {
    }

    public static String normalizeRadioStationName(String candidate) {
        if (candidate == null || candidate.isEmpty()) {
            return candidate;
        }
        candidate = candidate.replaceAll(SPACE, REPLACEMENT).replaceAll(TAB, EMPTY).replaceAll(QUESTION_MARK, EMPTY);
        candidate = candidate.endsWith(REPLACEMENT) ? candidate.replaceAll(SPACE, EMPTY) : candidate;
        candidate = candidate.endsWith(TAB) ? candidate.replaceAll(TAB, EMPTY) : candidate;
        return candidate;
    }

    public static int parseSafeInt(final String candidate) {
        try {
            return Integer.parseInt(candidate);
        } catch (Exception e) {
            //*gulp*
        }
        return DEFAULT;
    }

}
