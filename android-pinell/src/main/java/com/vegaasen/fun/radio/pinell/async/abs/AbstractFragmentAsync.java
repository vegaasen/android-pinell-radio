package com.vegaasen.fun.radio.pinell.async.abs;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.vegaasen.fun.radio.pinell.service.PinellService;

public abstract class AbstractFragmentAsync extends AsyncTask<Void, Void, Void> {

    private static final String TAG = AbstractFragmentAsync.class.getSimpleName();

    protected final View view;
    protected final PinellService pinellService;
    protected final String unknown;

    public AbstractFragmentAsync(View view, PinellService pinellService, String unknown) {
        super();
        if (view == null || unknown == null || pinellService == null) {
            Log.w(TAG, "Shits gonna fail :-)");
        }
        this.view = view;
        this.unknown = unknown;
        this.pinellService = pinellService;
    }

    protected abstract void configureViewComponents();

    @Override
    protected abstract void onPostExecute(Void aVoid);

}
