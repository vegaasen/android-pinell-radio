package com.vegaasen.http.rest.model.abs;

import com.vegaasen.http.rest.utils.StringUtils;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public abstract class KeyValueVariant {

    private final StringId key;
    private final StringId value;

    public KeyValueVariant(final String key, final String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Neither key or value can be nilled/empty.");
        }
        this.key = new StringId(key);
        this.value = new StringId(value);
    }

    public StringId getKey() {
        return key;
    }

    public StringId getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "KeyValueVariant{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
