package com.vegaasen.fun.radio.pinell.async.function;

import android.util.Log;
import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentVoidAsync;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * Simply handles the "GetNotifies" stuff, that is extremely strange. I have literally NO idea what it does
 * but it seems quite vital. *jeay*...
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 26.11.2015
 */
public class GetNotifiesAsync extends AbstractFragmentVoidAsync {

    private static final String TAG = GetNotifiesAsync.class.getSimpleName();

    public GetNotifiesAsync(PinellService pinellService) {
        super(pinellService);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "Triggering getNotifies");
        pinellService.triggerGetNotifies();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void configureViewComponents() {
    }
}
