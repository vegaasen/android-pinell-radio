package com.vegaasen.http.rest.model.auth;

import com.vegaasen.http.rest.model.abs.StringId;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public final class User {
    private final StringId userId;
    private final StringId password;

    private User(final Builder builder) {
        this.userId = builder.userId;
        this.password = builder.password;
    }

    public StringId getUserId() {
        return userId;
    }

    public StringId getPassword() {
        return password;
    }

    public static final class Builder {
        private StringId userId;
        private StringId password;

        public Builder userId(final String s) {
            this.userId = new StringId(s);
            return this;
        }

        public Builder password(final String s) {
            this.password = new StringId(s);
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

}
