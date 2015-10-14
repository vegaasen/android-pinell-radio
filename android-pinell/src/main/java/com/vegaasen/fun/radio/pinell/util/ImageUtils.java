package com.vegaasen.fun.radio.pinell.util;

import android.graphics.drawable.Drawable;
import android.util.Log;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public final class ImageUtils {

    private static final String TAG = ImageUtils.class.getName();
    private static final int NUM_THREADS = 1;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUM_THREADS);

    private ImageUtils() {
    }

    /**
     * Convert a candidate (from unknown sources) to a drawable
     *
     * @param candidate _
     * @return _
     */
    public static Drawable convert(final String candidate) {
        if (Strings.isNullOrEmpty(candidate)) {
            Log.i(TAG, "Unable to fetch images of nilled or empty sources");
            return null;
        }
        try {
            final Future<Drawable> submit = EXECUTOR_SERVICE.submit(new Callable<Drawable>() {
                @Override
                public Drawable call() throws Exception {
                    try {
                        return Drawable.createFromStream((InputStream) new URL(candidate).getContent(), candidate);
                    } catch (IOException e) {
                        Log.w(TAG, String.format("Unable to fetch and convert image from {%s}", candidate), e);
                    }
                    return null;
                }
            });
            while (!submit.isDone()) {
            }
            return submit.get();
        } catch (Exception e) {
            Log.w(TAG, String.format("Unable to fetch and convert image from {%s}", candidate), e);
            return null;
        }
    }

}
