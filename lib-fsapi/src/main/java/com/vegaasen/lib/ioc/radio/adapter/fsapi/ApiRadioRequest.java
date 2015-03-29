package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.constants.Parameter;
import com.vegaasen.lib.ioc.radio.adapter.constants.UriContext;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.response.Item;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.util.XmlUtils;
import org.w3c.dom.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ApiRadioRequest {

    INSTANCE;

    private static final String PREVIOUS_LEVEL = "0xffffffff";

    /**
     * Returns to the previous container. The property "PREVIOUS" is Pinell's own method of returning one level up
     *
     * @param host     _
     * @param maxItems _
     * @return _
     */
    public Set<RadioStation> getToPreviousContainer(Host host, int maxItems) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, PREVIOUS_LEVEL);
        ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.SELECT_SUB_CONTAINER, params));
        return getRadioStations(host, -1, maxItems);
    }

    /**
     * Normally, this will not be used. However, when dealing with internet-radios, they may have underlaying containers
     * of either just containers, or radio-stations. Example:
     * - My Favorites
     * -- Local
     * --- Radio Haugaland
     * --- NRK mP3
     * --- NRK P3
     * --- Radio 1
     * --- etc..
     *
     * @param host         _
     * @param radioStation where to go further from..? This may be "My Favorites".
     * @param maxItems     _
     * @return _
     */
    public Set<RadioStation> getSubContainers(Host host, RadioStation radioStation, int maxItems) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, radioStation.getKeyIdAsString());
        ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.SELECT_SUB_CONTAINER, params));
        return getRadioStations(host, -1, maxItems);
    }

    public Set<RadioStation> getRadioStations(Host host, int fromIndex, int maxItems) {
        preGetRadioStations(host);
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.MAX_ITEMS, Integer.toString(maxItems));
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(
                        host,
                        String.format(UriContext.RadioNavigation.STATION_LIST, fromIndex),
                        params
                )
        );
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            Set<RadioStation> radioStations = new HashSet<RadioStation>();
            for (final Item item : XmlUtils.INSTANCE.getItems(document.getDocumentElement())) {
                radioStations.add(RadioStation.create(item));
            }
            return radioStations;
        }
        return Collections.emptySet();
    }

    /**
     * For some odd reason, Pinell radios requires this sequence to be sent.
     * It looks a bit messy, just because it is a bit messy. Awesome..
     *
     * @param host _
     */
    private void preGetRadioStations(Host host) {
        try {
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_CAPS));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_STATE));
            final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
            params.put(Parameter.QueryParameter.VALUE, "0");
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_SET_NAV_STATE, params));
            params.put(Parameter.QueryParameter.VALUE, "1");
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_SET_NAV_STATE, params));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_STATE));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_STATUS));
        } catch (final Exception e) {
            //*gulp*
        }
    }

}
