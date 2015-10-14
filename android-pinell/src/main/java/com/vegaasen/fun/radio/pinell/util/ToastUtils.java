package com.vegaasen.fun.radio.pinell.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Simple Toast-utils that helps on generating the Toasts used around in the application
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 23.09.2015
 */
public final class ToastUtils {

    private ToastUtils() {
    }

    /**
     * Generate a toast based on a view layout (most likely the toast_simple) and a context
     *
     * @param toastLayout  what is the layout? - need to be inflated, most likely..
     * @param txtReference viewId?
     * @param context      context to operate within
     * @param message      the message itself
     */
    public static void generateToast(View toastLayout, int txtReference, Context context, String message) {
        TextView txtToastSimple = (TextView) toastLayout.findViewById(txtReference);
        if (txtToastSimple == null) {
            return;
        }
        txtToastSimple.setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }

}
