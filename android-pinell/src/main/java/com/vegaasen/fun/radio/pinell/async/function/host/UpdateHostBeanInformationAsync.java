package com.vegaasen.fun.radio.pinell.async.function.host;

import com.vegaasen.fun.radio.pinell.async.abs.AbstractFragmentAsync;
import com.vegaasen.fun.radio.pinell.discovery.model.HostBean;
import com.vegaasen.fun.radio.pinell.service.PinellService;

/**
 * Asynchronious hostBean
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class UpdateHostBeanInformationAsync extends AbstractFragmentAsync<HostBean> {

    private final HostBean hostBean;

    public UpdateHostBeanInformationAsync(PinellService pinellService, HostBean candidate) {
        super(pinellService);
        hostBean = candidate;
    }

    @Override
    protected void onPostExecute(HostBean aVoid) {
    }

    @Override
    protected void configureViewComponents() {
    }

    @Override
    protected HostBean doInBackground(HostBean... hostDetails) {
        pinellService.updateHostBeanDetails(hostBean);
        return hostBean;
    }

    public void silentGet() {
        try {
            get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
