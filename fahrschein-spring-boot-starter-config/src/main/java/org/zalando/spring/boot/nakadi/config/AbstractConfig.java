package org.zalando.spring.boot.nakadi.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

@Data
public abstract class AbstractConfig {

    private String id;

    private Boolean autostartEnabled;

    private String nakadiUrl;

    private String applicationName;

    private String consumerGroup;

    private Position readFrom;

    @NestedConfigurationProperty
    protected OAuthConfig oauth = OAuthConfig.defaultOAuthConfig();

    @NestedConfigurationProperty
    protected HttpConfig http = HttpConfig.defaultHttpConfig();

}
