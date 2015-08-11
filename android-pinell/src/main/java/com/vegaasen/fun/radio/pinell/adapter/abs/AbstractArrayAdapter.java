package com.vegaasen.fun.radio.pinell.adapter.abs;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;

import java.util.List;

/**
 * @author <a href="mailto:vegaasen@gmail.com">vegaasen</a>
 */
public abstract class AbstractArrayAdapter<T> extends ArrayAdapter<T> {

    public AbstractArrayAdapter(Context context, int resource, List<T> whatever) {
        super(context, resource);
    }

    public abstract void updateElements(List<T> hosts);

}
