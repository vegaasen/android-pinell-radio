package com.vegaasen.http.rest.utils;

import com.vegaasen.http.rest.handler.Restinator;
import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.abs.StringId;
import com.vegaasen.http.rest.model.auth.Authentication;
import com.vegaasen.http.rest.model.auth.AuthenticationType;
import com.vegaasen.http.rest.model.http.ContentType;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.RequestType;
import com.vegaasen.http.rest.model.http.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Simple utils that is used to convert the response to something usable.
 * <p/>
 * Example:
 * to string
 * to object
 * to this and that..
 * <p/>
 * COOL BEANS! :-)
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class HttpUtils {

    private static final int FIRST_INDEXED = 0;
    private static final String EMPTY = "";
    private static final int NUM_THREADS_ALIVE_ACTIVE = 1;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUM_THREADS_ALIVE_ACTIVE);
    private static final long TIMEOUT = 2;

    private HttpUtils() {
    }

    public static Response performGetLikeRequest(final RequestType requestType, final Scheme scheme) {
        verifyParamters(scheme, requestType);
        configureSchemeForGet(scheme);
        return (scheme.isHttpsActivated()) ? getHttpsConnection(requestType, scheme) : getHttpConnection(requestType, scheme);
    }

    public static Response performPostLikeRequest(final RequestType requestType, final Scheme scheme) {
        verifyParamters(scheme, requestType);
        return (scheme.isHttpsActivated()) ? getHttpsConnection(requestType, scheme) : getHttpConnection(requestType, scheme);
    }

    private static Response getHttpsConnection(final RequestType requestType, final Scheme scheme) {
        throw new IllegalStateException("HTTPS Connection is not yet implemented");
    }

    private static Response getHttpConnection(final RequestType requestType, final Scheme scheme) {
        try {
            final ResponseFetcher task = new ResponseFetcher(requestType, scheme);
            final Future<Response> future = EXECUTOR_SERVICE.submit(task);
            task.getCountDownLatch().await(TIMEOUT, TimeUnit.SECONDS);
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void conditionallyConfigurePostLikeRequest(
            final Scheme scheme,
            final URLConnection urlConnection,
            final RequestType requestType) {
        if (requestType.equals(RequestType.PUT) || requestType.equals(RequestType.POST) || requestType.equals(RequestType.DELETE)) {
            urlConnection.addRequestProperty(Header.HEADER_CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getVariant());
            if (scheme.getParams() == null || scheme.getParams().isEmpty()) {
                return;
            }
            final StringBuilder builder = new StringBuilder();
            for (final Param param : scheme.getParams()) {
                if (builder.length() != 0) {
                    builder.append(UrlBuilder.AMP);
                }
                for (final StringId id : param.getValue()) {
                    builder.append(param.getKey().getId()).append(UrlBuilder.EQ).append(id.getId());
                    if (!UrlBuilder.getInstance().lastIndexOf(id, param.getValue())) {
                        builder.append(UrlBuilder.VALUE_SEPARATOR);
                    }
                }
            }
            try {
                urlConnection.getOutputStream().write(builder.toString().getBytes());
            } catch (final IOException e) {
                //oh snap!
            }
        }
    }

    private static void configureConnection(final URLConnection urlConnection) {
        urlConnection.setConnectTimeout(Restinator.getConnectionTimeout());
        urlConnection.setReadTimeout(Restinator.getSocketTimeout());
    }

    private static void appendHeadersForConnection(final URLConnection connection, final Scheme scheme) {
        if (scheme.getHeaders() == null || scheme.getHeaders().isEmpty()) {
            return;
        }
        for (final Header header : scheme.getHeaders()) {
            connection.addRequestProperty(header.getKey().getId(), header.getValue().getId());
        }
    }

    private static Map<String, String> convertHeaders(final Map<String, List<String>> headers) {
        if (headers != null && !headers.isEmpty()) {
            final Map<String, String> convertedHeaders = new HashMap<>();
            for (final Map.Entry<String, List<String>> header : headers.entrySet()) {
                if (header != null) {
                    if (header.getValue().size() == 1) {
                        convertedHeaders.put(header.getKey(), header.getValue().get(FIRST_INDEXED));
                    }
                }
            }
            return convertedHeaders;
        }
        return Collections.emptyMap();
    }

    private static void conditionallyAddAuthenticationMechanism(final Authentication authentication) {
        if (authentication == null ||
                !authentication.getAuthenticationType().equals(AuthenticationType.BASIC) ||
                authentication.getUser() == null) {
            return;
        }
        Authenticator.setDefault(new UsernamePasswordAuthenticator(authentication.getUser()));
    }

    private static String convertInputStreamToPayload(final InputStream stream) {
        if (stream == null) {
            return EMPTY;
        }
        final InputStreamReader reader = new InputStreamReader(stream);
        final StringBuilder builder = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String read;
            read = bufferedReader.readLine();
            while (read != null) {
                builder.append(read);
                read = bufferedReader.readLine();

            }
        } catch (final IOException e) {
            //eaten! NOMMNOMMNOMM!!
        }
        return builder.toString();
    }

    private static void configureSchemeForGet(final Scheme scheme) {
        scheme.setTo(scheme.compileAsString());
    }

    private static void verifyParamters(final Scheme scheme, final RequestType requestType) {
        if (requestType == null || scheme == null) {
            throw new IllegalArgumentException("Either requestType or Scheme may be nilled");
        }
    }

    private static final class ResponseFetcher implements Callable<Response> {

        private final CountDownLatch countDownLatch;
        private final RequestType requestType;
        private final Scheme scheme;

        public ResponseFetcher(RequestType requestType, Scheme scheme) {
            countDownLatch = new CountDownLatch(NUM_THREADS_ALIVE_ACTIVE);
            this.requestType = requestType;
            this.scheme = scheme;
        }

        @Override
        public Response call() throws Exception {
            return getResponse();
        }

        private Response getResponse() throws UnknownHostException {
            HttpURLConnection urlConnection = null;
            try {
                conditionallyAddAuthenticationMechanism(scheme.getAuthentication());
                urlConnection = (HttpURLConnection) scheme.getTo().openConnection();
                urlConnection.setRequestMethod(requestType.getType());
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setUseCaches(true);
                urlConnection.setDoOutput(false);
                configureConnection(urlConnection);
                appendHeadersForConnection(urlConnection, scheme);
                conditionallyConfigurePostLikeRequest(scheme, urlConnection, requestType);
                final Response response = new Response();
                final InputStream inputStream = urlConnection.getInputStream();
                response.setResponseCode(urlConnection.getResponseCode());
                response.setWhen(urlConnection.getIfModifiedSince());
                response.setOriginalRequestScheme(scheme);
                response.setHeaders(convertHeaders(urlConnection.getHeaderFields()));
                response.setPayload(convertInputStreamToPayload(inputStream));
                response.setOriginalPayload(inputStream);
                return response;
            } catch (final IOException e) {
                throw new IllegalArgumentException(String.format("The URL {%s} is for some reason not parseable", scheme.getTo().toString()), e);
            } finally {
                countDownLatch.countDown();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        public CountDownLatch getCountDownLatch() {
            return countDownLatch;
        }
    }

}
