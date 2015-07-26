package com.vegaasen.http.rest.model.abs;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class StringId {

    private String id;

    public StringId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringId stringId = (StringId) o;

        if (id != null ? !id.equals(stringId.id) : stringId.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
