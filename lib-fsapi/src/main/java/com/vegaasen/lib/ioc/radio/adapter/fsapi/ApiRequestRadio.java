package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
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

/**
 * Todo: beautify.
 */
public enum ApiRequestRadio {

    INSTANCE;

    private static final String PREVIOUS_LEVEL = "0xffffffff";
    private static final String FM_SEARCH_FORWARD = "3";
    private static final String FM_SEARCH_REWIND = "4";
    public static final String EMPTY = "";

    public void searchFMForward(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, FM_SEARCH_FORWARD);
        ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.FM.SEEK_FORWARD, params));
    }

    public void searchFMRewind(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, FM_SEARCH_REWIND);
        ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.FM.SEEK_REWIND, params));
    }

    public String getCurrentFMBand(Host host) {
        final Document currentBand = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NOTIFIES));
        if (currentBand != null) {
            final Set<Item> items = XmlUtils.INSTANCE.getNotifyItems(currentBand.getDocumentElement(), ApiResponse.RadioStation.MEGAHERTZ, ApiResponse.RadioStation.PROPERTY_NAME);
            if (items != null) {
                final Item item = items.iterator().next();
                if (item.getFields() != null && !item.getFields().isEmpty()) {
                    return item.getFields().get(ApiResponse.RadioStation.MEGAHERTZ);
                }
            }
        }
        return EMPTY;
    }

    /**
     * Returns to the previous container. The property "PREVIOUS" is Pinell's own method of returning one level up
     * Example:
     * (from) -- Local
     * (to) - My Favorites
     *
     * @param host     _
     * @param maxItems _
     * @return _
     */
    public Set<RadioStation> selectPreviousContainer(Host host, int maxItems) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, PREVIOUS_LEVEL);
        ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.SUB_CONTAINER_SELECT, params));
        return getRadioStations(host, -1, maxItems);
    }

    /**
     * Normally, this will not be used. However, when dealing with internet-radios, they may have underlying containers
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
    public Set<RadioStation> selectContainerAndGetRadioStations(Host host, RadioStation radioStation, int maxItems) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, radioStation.getKeyIdAsString());
        ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.SUB_CONTAINER_SELECT, params));
        return getRadioStations(host, -1, maxItems);
    }

    //TODO: There is, for some reason, something not working as it should regarding the listing of radio stations/containers. Look into the sniffing session
    public Set<RadioStation> getRadioStations(Host host, int fromIndex, int maxItems) {
        preGenericRadioStations(host);
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.MAX_ITEMS, Integer.toString(maxItems));
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(
                        host,
                        String.format(UriContext.RadioNavigation.STATION_LIST, fromIndex),
                        params
                )
        );
        try {
            if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
                Set<RadioStation> radioStations = new HashSet<>();
                for (final Item item : XmlUtils.INSTANCE.getItems(document.getDocumentElement())) {
                    final RadioStation candidate = RadioStation.create(item);
                    if (candidate != null) {
                        radioStations.add(candidate);
                    }
                }
                return radioStations;
            }
        } finally {
            postGenericRadioStations(host);
        }
        return Collections.emptySet();
    }

    public void selectRadioStation(Host host, RadioStation radioStation) {
        preGenericRadioStations(host);
        try {
            if (host == null || radioStation == null) {
                return;
            }
            final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
            params.put(Parameter.QueryParameter.VALUE, radioStation.getKeyIdAsString());
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.STATION_SELECT, params));
        } finally {
            postSelectRadioStation(host);
        }
    }

    /**
     * For some odd reason, Pinell radios requires this sequence to be sent when dealing with radioStations.
     * It looks a bit messy, just because it is a bit messy. Awesome..
     *
     * @param host _
     */
    private void preGenericRadioStations(Host host) {
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
            try {
                //todo: messy? Oh, never!!! :-) for some reason, the get_notifies may fail on strange occasions. Wrapping this crappy stuff
                //todo: create a common wrapper for callables..?
                ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NOTIFIES));
            } catch (Exception e) {
                //*gulp*
            }
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NUM_ITEMS));
        } catch (final Exception e) {
            //*gulp*
        }
    }

    /**
     * This is some of the required API calls that have to be issued after showing the various RadioStations
     * At least, that is what it looks like.
     *
     * @param host _
     */
    private void postGenericRadioStations(Host host) {
        try {
            final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
            params.put(Parameter.QueryParameter.VALUE, "0");
            ApiConnection.INSTANCE.requestAsync(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_SET_NAV_STATE, params));
        } catch (final Exception e) {
            //*gulp*
        }
    }

    /**
     * For some reason, we also need to ensure that some of the APIs is getting requested. Atleast, that is how it looks like in the proxy listner application
     *
     * @param host _
     */
    private void postSelectRadioStation(Host host) {
        try {
            ApiConnection.INSTANCE.requestAsync(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NOTIFIES));
            postGenericRadioStations(host);
        } catch (Exception e) {
            // *gulp*
        }
    }

}
