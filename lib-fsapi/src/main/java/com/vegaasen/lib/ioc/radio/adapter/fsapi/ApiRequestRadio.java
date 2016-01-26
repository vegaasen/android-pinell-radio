package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.adapter.constants.Parameter;
import com.vegaasen.lib.ioc.radio.adapter.constants.UriContext;
import com.vegaasen.lib.ioc.radio.model.annotation.Beta;
import com.vegaasen.lib.ioc.radio.model.dab.RadioStation;
import com.vegaasen.lib.ioc.radio.model.response.Item;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.util.RandomUtils;
import com.vegaasen.lib.ioc.radio.util.XmlUtils;
import org.w3c.dom.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The idea behind this class is to gather all the various API-Requests that exists within the Radio-atmosphere
 * <p/>
 * Todo: Review. Does this even work as intended? There are quite a few bugs here, surely this can be done better?
 *
 * @version 26.11.2015
 * @since 9.2.2015
 */
public enum ApiRequestRadio {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(ApiRequestRadio.class.getSimpleName());
    private static final String PREVIOUS_HIERARCHY_LEVEL = "0xffffffff";
    private static final String FM_SEARCH_FORWARD = "3", FM_SEARCH_REWIND = "4";
    private static final String RANDOM = RandomUtils.randomAsString(), EMPTY = "";
    private static final int MAX_TRIES = 5, AWAITING_READYNESS = 50;

    public void searchFMForward(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, FM_SEARCH_FORWARD);
        ApiConnection.INSTANCE.requestAsync(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.FM.SEEK_FORWARD, params));
    }

    public void searchFMRewind(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, FM_SEARCH_REWIND);
        ApiConnection.INSTANCE.requestAsync(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.FM.SEEK_REWIND, params));
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
        params.put(Parameter.QueryParameter.VALUE, PREVIOUS_HIERARCHY_LEVEL);
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
        preGenericRadioStations(host);
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, radioStation.getKeyIdAsString());
        ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.SUB_CONTAINER_SELECT, params));
        awaitRadioReady(host);
        return getRadioStations(host, -1, maxItems, true);
    }

    public Set<RadioStation> getRadioStations(Host host, int fromIndex, int maxItems) {
        return getRadioStations(host, fromIndex, maxItems, false);
    }

    public Set<RadioStation> getRadioStations(Host host, int fromIndex, int maxItems, boolean container) {
        if (container) {
            LOG.info("Container selected, treating it as such");
            preFolderRadioStations(host);
        } else {
            LOG.info("Normal loading operation, loading stations for the first time/default list");
            preGenericRadioStations(host);
        }
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.MAX_ITEMS, Integer.toString(maxItems));
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(
                        host,
                        String.format(UriContext.RadioNavigation.STATION_LIST, fromIndex),
                        params
                )
        );
        Set<RadioStation> radioStations = new HashSet<>();
        try {
            if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
                for (final Item item : XmlUtils.INSTANCE.getItems(document.getDocumentElement())) {
                    final RadioStation candidate = RadioStation.create(item);
                    if (candidate != null) {
                        radioStations.add(candidate);
                    }
                }
            }
        } finally {
            postGenericRadioStations(host);
        }
        return radioStations;
    }

    public void selectRadioStation(Host host, RadioStation radioStation) {
        preGenericRadioStations(host);
        try {
            if (host == null || radioStation == null) {
                return;
            }
            final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
            params.put(Parameter.QueryParameter.VALUE, radioStation.getKeyIdAsString());
            ApiConnection.INSTANCE.requestAsync(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.STATION_SELECT, params));
        } finally {
            postGenericRadioStations(host);
        }
    }

    /**
     * Checks if anything has changed upon the last time checked. This results most of the times in the following response:
     * FS_TIMEOUT (after 30ish seconds)
     *
     * @param host _
     */
    @Deprecated
    @Beta
    public void getNotifies(Host host) {
        try {
            final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
            params.put(Parameter.QueryParameter.RANDOM, RANDOM);
            ApiConnection.INSTANCE.requestAsync(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NOTIFIES));
        } catch (Exception e) {
            LOG.info("Unable to getNotifies");
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
            LOG.info(String.format("Fetching {%s}", UriContext.RadioNavigation.PRE_GET_NAV_STATE));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_STATE));
            final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
            params.put(Parameter.QueryParameter.VALUE, "0");
            LOG.info(String.format("Setting {%s} to 0", UriContext.RadioNavigation.PRE_SET_NAV_STATE));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_SET_NAV_STATE, params));
            params.put(Parameter.QueryParameter.VALUE, "1");
            LOG.info(String.format("Setting {%s} to 1", UriContext.RadioNavigation.PRE_SET_NAV_STATE));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_SET_NAV_STATE, params));
            awaitRadioReady(host);
            LOG.info(String.format("Fetching {%s}", UriContext.RadioNavigation.PRE_GET_NAV_DEPTH));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_DEPTH));
            LOG.info(String.format("Fetching {%s}", UriContext.RadioNavigation.PRE_GET_NUM_ITEMS));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NUM_ITEMS));
        } catch (final Exception e) {
            LOG.info("Unable to execute pre-generics for radio stations");
        }
    }

    private void preFolderRadioStations(Host host) {
        try {
            LOG.info(String.format("Fetching {%s}", UriContext.RadioNavigation.PRE_GET_NAV_STATE));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_STATE));
            LOG.info(String.format("Fetching {%s}", UriContext.RadioNavigation.PRE_GET_NAV_DEPTH));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_DEPTH));
            LOG.info(String.format("Fetching {%s}", UriContext.RadioNavigation.PRE_GET_NUM_ITEMS));
            ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NUM_ITEMS));
        } catch (Exception e) {
            LOG.info("Unable to execute pre-folder for radio stations");
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
            LOG.warning("Unable to execute post-generics for radio station");
        }
    }

    private void awaitRadioReady(Host host) {
        int tries = 0;
        while (tries <= MAX_TRIES) {
            Document candidate = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.RadioNavigation.PRE_GET_NAV_STATUS));
            if (candidate != null) {
                if (XmlUtils.INSTANCE.getTextContentByNode(candidate.getDocumentElement(), ApiResponse.VALUE_U_8).equals(ApiResponse.Value.TRUE)) {
                    return;
                }
                try {
                    Thread.sleep(AWAITING_READYNESS);
                } catch (InterruptedException e) {
                    // * gulp *
                }
            }
            LOG.info(String.format("Try {%s}", tries));
            tries++;
        }
    }

}
