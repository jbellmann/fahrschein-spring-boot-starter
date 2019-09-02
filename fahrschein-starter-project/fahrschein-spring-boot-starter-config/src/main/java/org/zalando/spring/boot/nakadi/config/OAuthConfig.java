package org.zalando.spring.boot.nakadi.config;

import java.util.Optional;

import lombok.Data;

@Data
public class OAuthConfig {

    private Boolean enabled;

    private String accessTokenId;

    public static OAuthConfig defaultOAuthConfig() {
        OAuthConfig c = new OAuthConfig();
        c.setEnabled(Boolean.FALSE);

        return c;
    }

    public void setAccessTokenIdIfNotConfigured(String key) {
        this.setAccessTokenId(Optional.ofNullable(this.getAccessTokenId()).orElse(key));
    }

}
