package org.zalando.spring.boot.nakadi.config.properties;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public abstract class AbstractConfig {

    private String id;

    private Boolean autostartEnabled;

    private String nakadiUrl;

    private String applicationName;

    private String consumerGroup;

    private Position readFrom;

    private Boolean recordMetrics;

    @NestedConfigurationProperty
    protected OAuthConfig oauth = OAuthConfig.defaultOAuthConfig();

    @NestedConfigurationProperty
    protected HttpConfig http = HttpConfig.defaultHttpConfig();

    @NestedConfigurationProperty
    protected AuthorizationsConfig authorizations = new AuthorizationsConfig();

    @NestedConfigurationProperty
    protected BackoffConfig backoff = BackoffConfig.defaultBackoffConfig();

    protected ThreadConfig threads = new ThreadConfig();

}
