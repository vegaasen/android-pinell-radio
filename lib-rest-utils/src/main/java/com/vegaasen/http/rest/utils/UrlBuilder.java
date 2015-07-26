package com.vegaasen.http.rest.utils;

import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.abs.StringId;
import com.vegaasen.http.rest.model.http.Param;
import com.vegaasen.http.rest.model.http.UrlParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class UrlBuilder<T> {

    public static final String AMP = "&", FT = "?", EQ = "=", VALUE_SEPARATOR = ";";
    public static final String R = "{", L = "}";

    private static final String EMPTY = "";
    private static final int FIRST = 0;
    private static final UrlBuilder builder;

    static {
        builder = new UrlBuilder();
    }

    private UrlBuilder() {
    }

    public static UrlBuilder getInstance() {
        return builder;
    }

    public static String buildFromScheme(final Scheme scheme) {
        if (scheme == null || scheme.getTo() == null) {
            throw new IllegalArgumentException("Scheme cannot be nilled");
        }
        return String.format(
                "%s%s",
                conditionallyReplaceUrlParams(scheme.getUrlParams(), scheme.getTo().toString()),
                appendParams(scheme.getParams())
        );
    }

    public static URI buildFromSchemeAsUri(final Scheme scheme) throws URISyntaxException {
        return new URI(buildFromScheme(scheme));
    }

    private static String conditionallyReplaceUrlParams(final List<UrlParam> urlParams, final String url) {
        if (urlParams == null || urlParams.isEmpty()) {
            return url;
        }
        String remastered = url;
        for (final UrlParam urlParam : urlParams) {
            final String key = String.format("%s%s%s", R, urlParam.getKey().getId(), L);
            if (remastered.contains(key)) {
                remastered = remastered.replace(key, urlParam.getValue().getId());
            }
        }
        return remastered;
    }

    @SuppressWarnings("unchecked")
    private static String appendParams(final List<Param> params) {
        if (params == null || params.size() == 0) {
            return EMPTY;
        }
        final StringBuilder builder = new StringBuilder();
        for (final Param param : params) {
            builder
                    .append(param.getKey().getId())
                    .append(EQ).append(appendValues(param.getValue()))
                    .append((getInstance().lastIndexOf(param, params)) ? EMPTY : AMP);
        }
        return builder.insert(FIRST, FT).toString();
    }

    @SuppressWarnings("unchecked")
    private static String appendValues(List<StringId> values) {
        if (values == null || values.size() == 0) {
            return EMPTY;
        }
        final StringBuilder builder = new StringBuilder();
        for (final StringId stringId : values) {
            builder.append(stringId.getId()).append((getInstance().lastIndexOf(stringId, values)) ? EMPTY : VALUE_SEPARATOR);
        }
        return builder.toString();
    }

    //todo: generify
    public boolean lastIndexOf(final T o, final List<T> os) {
        return os == null || o == null || os.indexOf(o) == (os.size() - 1);
    }

}
