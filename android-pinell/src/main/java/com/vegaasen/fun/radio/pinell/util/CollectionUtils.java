package com.vegaasen.fun.radio.pinell.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Simple collectionUtils that I use within the app here and there. Jeah, some of these may exists already in some other library, I just didnt bother looking :P
 *
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> List<T> toList(Set<T> candidate) {
        List<T> converted = new ArrayList<>(candidate.size());
        converted.addAll(candidate);
        return converted;
    }

    public static <T> void stripNilledElements(List<T> candidate) {
        if (candidate == null) {
            return;
        }
        int what = candidate.indexOf(null);
        if (what > 0) {
            candidate.remove(what);
        }
    }

    public static <T> boolean isEmpty(Collection<T> candidate) {
        return candidate == null || candidate.isEmpty();
    }

}
