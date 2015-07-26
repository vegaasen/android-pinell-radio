package com.vegaasen.http.rest.model.abs;

import com.vegaasen.http.rest.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public abstract class ExtendedKeyValueVariant {

    private final StringId key;
    private final List<StringId> value = new ArrayList<>();

    public ExtendedKeyValueVariant(final String key, final String... value) {
        if (StringUtils.isBlank(key) || value.length == 0) {
            throw new IllegalArgumentException("Neither key or value can be nilled/empty.");
        }
        this.key = new StringId(key);
        for (final String v : value) {
            this.value.add(new StringId(v));
        }
    }

    public StringId getKey() {
        return key;
    }

    public List<StringId> getValue() {
        return value;
    }

}
