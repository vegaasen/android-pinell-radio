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
 * Todo: errohandling, errorhandling, errorhandling.
 * Todo: Prettify
 *
 * @author vegaasen
 */
public enum ApiSystemRequest {

    INSTANCE;

    private static final String EMPTY = "";
    private static final String MIN = "-1", MAX = Integer.toString(Integer.MAX_VALUE);

    public Equalizer getCurrentEqualizer(Host host) {
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.System.GET_EQUALIZER));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            return Equalizer.create(
                    Item.create(
                            XmlUtils.INSTANCE.getNumberContentByNode(document.getDocumentElement(), ApiResponse.VALUE_U_8), Collections.<String, String>emptyMap()
                    )
            );
        }
        return null;
    }

    public boolean setEqualizer(Host host, Equalizer equalizer) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.VALUE, equalizer.getKeyAsString());
        final Document document = ApiConnection.INSTANCE.request(ApiConnection.INSTANCE.getApiUri(host, UriContext.System.SET_EQUALIZER, params));
        return document != null && ApiConnection.INSTANCE.verifyResponseOk(document);
    }

    public Set<Equalizer> getEqualizers(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.MAX_ITEMS, MAX);
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(host, String.format(UriContext.System.VALID_EQUALIZERS, MIN), params));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            Set<Equalizer> radioStations = new HashSet<Equalizer>();
            for (final Item item : XmlUtils.INSTANCE.getItems(document.getDocumentElement())) {
                radioStations.add(Equalizer.create(item));
            }
            return radioStations;
        }
        return Collections.emptySet();
    }

    public Set<RadioMode> getRadioModes(Host host) {
        final Map<String, String> params = ApiConnection.INSTANCE.getDefaultApiConnectionParams(host);
        params.put(Parameter.QueryParameter.MAX_ITEMS, MAX);
        final Document document = ApiConnection.INSTANCE.request(
                ApiConnection.INSTANCE.getApiUri(host, String.format(UriContext.System.VALID_MODES, MIN), params));
        if (document != null && ApiConnection.INSTANCE.verifyResponseOk(document)) {
            Set<RadioMode> radioStations = new HashSet<RadioMode>();
            for (final Item item : XmlUtils.INSTANCE.getItems(document.getDocumentElement())) {
                radioStations.add(RadioMode.create(item));
            }
            return radioStations;
        }
        return Collections.emptySet();
    }

    public DeviceAudio getDeviceAudioInformation(Host host) {
        return DeviceAudio.create(
                AudioStatus.fromValue(getResponse(host, UriContext.Device.AUDIO_VOLUME_MUTED)),
                Integer.parseInt(getResponse(host, UriContext.Device.AUDIO_VOLUME_LEVEL))
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

    private void removeSession(Host host) {
        host.removeRadioSession();
    }

}
