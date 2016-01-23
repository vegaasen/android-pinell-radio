package com.vegaasen.fun.radio.pinell.async.abs;

import android.support.v4.app.FragmentManager;
import android.view.View;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public abstract class AbstractFragmentVoidAsync extends AbstractFragmentAsync<Void> {

    public AbstractFragmentVoidAsync(FragmentManager fragmentManager, View view, PinellService pinellService, String unknown) {
        super(fragmentManager, view, pinellService, unknown);
    }

    public AbstractFragmentVoidAsync(PinellService pinellService) {
        super(pinellService);
    }
}
