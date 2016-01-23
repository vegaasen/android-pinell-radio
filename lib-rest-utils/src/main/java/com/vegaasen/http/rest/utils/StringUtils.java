package com.vegaasen.http.rest.utils;

/**
 * Instead of using some external lib for this stuff, I will just use my own implementation of the
 * same stuff as various other libs is offering - to keep stuff as minimalized as possible + avoid deps.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isBlank(final String... candidates) {
        for (final String candidate : candidates) {
            if (isBlank(candidate)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlank(final String candidate) {
        return isNull(candidate) || candidate.isEmpty() || candidate.trim().isEmpty();
    }

    public static boolean isNull(final String candidate) {
        return candidate == null;
    }

    public static boolean equalsTrimmed(final String what, final String candidate) {
        return !isBlank(what, candidate) && trim(what).equals(trim(candidate));
    }

    public static String trim(final String candidate) {
        return isBlank(candidate) ? candidate : candidate.trim();
    }

}
