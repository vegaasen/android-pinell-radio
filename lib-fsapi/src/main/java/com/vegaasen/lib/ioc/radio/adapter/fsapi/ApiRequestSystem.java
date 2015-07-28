package com.vegaasen.lib.ioc.radio.adapter.fsapi;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.adapter.constants.Parameter;
import com.vegaasen.lib.ioc.radio.adapter.constants.UriContext;
import com.vegaasen.lib.ioc.radio.model.device.AudioStatus;
import com.vegaasen.lib.ioc.radio.model.device.DeviceAudio;
import com.vegaasen.lib.ioc.radio.model.device.DeviceCurrentlyPlaying;
import com.vegaasen.lib.ioc.radio.model.device.DeviceInformation;
import com.vegaasen.lib.ioc.radio.model.response.Item;
import com.vegaasen.lib.ioc.radio.model.system.Equalizer;
import com.vegaasen.lib.ioc.radio.model.system.PowerState;
import com.vegaasen.lib.ioc.radio.model.system.RadioMode;
import com.vegaasen.lib.ioc.radio.model.system.connection.Host;
import com.vegaasen.lib.ioc.radio.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple class which basically just connects to the API. All of these methods requires a host is present
 * Todo: errohandling, errorhandling, errorhandling!!!!!!!!! ;-)
 * Todo: Prettify
 *
 * @author vegaasen
 * @version 26.07.2015
 */
public enum ApiRequestSystem {

    INSTANCE;

    private static final String EMPTY = "";
    private static final String MIN = "-1", MAX = Integer.toString(Integer.MAX_VALUE);

    public Set<Equalizer> getEqualizers(Host host) {
        if (host == null) {
            return Collections.emptySet();
        }
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.MAX_ITEMS, MAX);
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(host, String.format(UriContext.System.EQUALIZER_LIST, MIN), params));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            Set<Equalizer> equalizers = new HashSet<>();
            for (final Item item : XmlUtils.INSTANCE.getItems(document.getDocumentElement())) {
                final Equalizer candidate = Equalizer.create(item);
                if (candidate != null) {
                    equalizers.add(candidate);
                }
            }
            return equalizers;
        }
        return Collections.emptySet();
    }

    public Equalizer getEqualizer(Host host) {
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.System.EQUALIZER));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            final Equalizer equalizer = Equalizer.create(
                    Item.create(
                            XmlUtils.INSTANCE.getNumberContentByNode(document.getDocumentElement(), ApiResponse.VALUE_U_8),
                            Collections.<String, String>emptyMap()
                    )
            );
            if (equalizer != null) {
                for (final Equalizer candidate : getEqualizers(host)) {
                    if (candidate != null)
                        if (candidate.getKey() == equalizer.getKey()) {
                            return candidate;
                        }
                }
                return equalizer;
            }
        }
        return null;
    }

    public boolean setEqualizer(Host host, Equalizer equalizer) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, equalizer.getKeyAsString());
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.System.EQUALIZER_SET, params));
        return document != null && ApiConnection.INSTANCE.verifyResponseOk(document);
    }

    public Set<RadioMode> getRadioModes(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.MAX_ITEMS, MAX);
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(host, String.format(UriContext.System.RADIO_MODE_LIST, MIN), params));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            Set<RadioMode> radioStations = new HashSet<>();
            for (final Item item : XmlUtils.INSTANCE.getItems(document.getDocumentElement())) {
                final RadioMode e = RadioMode.create(item);
                //todo: should this actually be ignored, or should it rather be up to the service showing the stuff..? (e.g Android Adapter or similar)
                if (e.isSelectable()) {
                    radioStations.add(e);
                }
            }
            return radioStations;
        }
        return Collections.emptySet();
    }

    public RadioMode getRadioMode(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(host, UriContext.System.RADIO_MODE, params));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            RadioMode radioMode = RadioMode.create(Item.create(
                    XmlUtils.INSTANCE.getNumberContentByNode(document.getDocumentElement(), ApiResponse.VALUE_U_32),
                    Collections.<String, String>emptyMap()
            ));
            for (final RadioMode candidate : getRadioModes(host)) {
                if (candidate.getKey() == radioMode.getKey()) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public boolean setRadioMode(Host host, RadioMode radioMode) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, radioMode.getKeyAsString());
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.System.RADIO_MODE_SET, params));
        return document != null && ApiConnection.INSTANCE.verifyResponseOk(document);
    }

    public DeviceAudio getDeviceAudioInformation(Host host) {
        final String volumeLevelCandidate = getResponse(host, UriContext.Device.AUDIO_VOLUME_LEVEL);
        return DeviceAudio.create(
                AudioStatus.fromValue(getResponse(host, UriContext.Device.AUDIO_VOLUME_MUTED)),
                volumeLevelCandidate == null || volumeLevelCandidate.isEmpty() ? 0 : Integer.parseInt(volumeLevelCandidate)
        );
    }

    public DeviceInformation getDeviceInformation(Host host) {
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUriWithoutRoot(host, UriContext.Device.INFORMATION));
        if (document != null) {
            final Element documentElement = document.getDocumentElement();
            return DeviceInformation.create(
                    XmlUtils.INSTANCE.getTextContentByNode(documentElement, ApiResponse.Device.FRIENDLY_NAME),
                    XmlUtils.INSTANCE.getTextContentByNode(documentElement, ApiResponse.Device.VERSION),
                    XmlUtils.INSTANCE.getTextContentByNode(documentElement, ApiResponse.Device.API_REFERENCE)
            );
        }
        return null;
    }

    public boolean setDeviceAudioLevel(Host host, int level) {
        if (host == null || level < 0) {
            return false;
        }
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, Integer.toString(level));
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.Device.AUDIO_VOLUME_LEVEL_SET, params));
        return document != null && ApiConnection.INSTANCE.verifyResponseOk(document);
    }

    public DeviceCurrentlyPlaying getCurrentlyPlaying(Host host) {
        return DeviceCurrentlyPlaying.create(
                System.currentTimeMillis(),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_NAME).trim(),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_TEXT),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_GRAPHICAL),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_RATE),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_CAPS),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_STATUS),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_FREQUENCY),
                getResponse(host, UriContext.Device.CURRENTLY_PLAYING_DAB_SERVICE_ID),
                Integer.parseInt(getResponse(host, UriContext.Device.CURRENTLY_PLAYING_DURATION))
        );
    }

    /**
     * Get the current powerState. Might be On or Off.
     *
     * @param host _
     * @return UNKNOWN - which most likely denotes that the current session has timed out or is invalid.
     */
    public PowerState getCurrentPowerState(Host host) {
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.System.GET_POWER_STATE));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            return PowerState.fromState(XmlUtils.INSTANCE.getNumberContentByNode(document.getDocumentElement(), ApiResponse.VALUE_U_8));
        }
        return PowerState.UNKNOWN;
    }

    public boolean setPowerStateForDevice(Host host, PowerState state) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, state.getStateAsString());
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.System.SET_POWER_STATE, params));
        return document != null && ApiConnection.INSTANCE.verifyResponseOk(document);
    }

    public String obtainNewDeviceSession(Host host) {
        ApiConnection.INSTANCE.retrieveNewSession(host);
        return host.getRadioSession().getId();
    }

    private String getResponse(Host host, String where) {
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, where));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            return XmlUtils.INSTANCE.getTextContentByNode(document.getDocumentElement(), ApiResponse.VALUE);
        }
        return EMPTY;
    }

    private void revokeSession(Host host) {
        host.removeRadioSession();
    }

}
