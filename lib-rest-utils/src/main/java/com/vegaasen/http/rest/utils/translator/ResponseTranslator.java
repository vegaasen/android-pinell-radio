package com.vegaasen.http.rest.utils.translator;

import com.vegaasen.http.rest.model.http.Response;
import com.vegaasen.http.rest.utils.InputUtils;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

import java.util.logging.Logger;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 * @since 24.1.2016@1.0-SNAPSHOT
 */
public class ResponseTranslator {

    private static final Logger LOG = Logger.getLogger(ResponseTranslator.class.getSimpleName());

    private ResponseTranslator() {
    }

    public static Response translate(HttpResponse response) {
        if (response == null) {
            return null;
        }
        Response candidate = new Response();
        for (Header header : response.getAllHeaders()) {
            candidate.addHeader(header.getName(), header.getValue());
        }
        candidate.setResponseCode(response.getStatusLine().getStatusCode());
        candidate.setWhen(System.currentTimeMillis());
        try {
            candidate.setOriginalPayload(response.getEntity().getContent());
            candidate.setPayload(InputUtils.convertInputStreamToPayload(candidate.getOriginalPayload()));
        } catch (Exception e) {
            LOG.warning("Unable to convert httpResponse->response");
        }

        return candidate;
    }

}
