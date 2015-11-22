package com.vegaasen.fun.radio.pinell.async.abs;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import com.vegaasen.fun.radio.pinell.service.PinellService;

public abstract class AbstractFragmentAsync<T> extends AsyncTask<T, T, T> {

    private static final String TAG = AbstractFragmentAsync.class.getSimpleName();

    protected final View view;
    protected final PinellService pinellService;
    protected final String unknown;
    protected final FragmentManager fragmentManager;

    public AbstractFragmentAsync(PinellService pinellService) {
        super();
        if (pinellService == null) {
            Log.w(TAG, "Shits gonna fail :-)! Missing required stuff for the async operations");
        }
        view = null;
        unknown = null;
        fragmentManager = null;
        this.pinellService = pinellService;
    }

    public AbstractFragmentAsync(FragmentManager fragmentManager, View view, PinellService pinellService, String unknown) {
        super();
        if (view == null || unknown == null || pinellService == null || fragmentManager == null) {
            Log.w(TAG, "Shits gonna fail :-)! Missing required stuff for the async operations");
        }
        this.view = view;
        this.unknown = unknown;
        this.pinellService = pinellService;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected abstract void onPostExecute(T aVoid);

    protected abstract void configureViewComponents();

}
