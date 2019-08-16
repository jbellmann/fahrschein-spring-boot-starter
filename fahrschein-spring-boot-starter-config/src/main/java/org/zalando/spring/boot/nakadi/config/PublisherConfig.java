package org.zalando.spring.boot.nakadi.config;

import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PublisherConfig extends AbstractConfig {

    public PublisherConfig() {
        setId("default");
    }

    void mergeWithDefaultConfig(DefaultConsumerConfig defaultConsumerConfig) {
        this.setNakadiUrl(Optional.ofNullable(this.getNakadiUrl()).orElse(defaultConsumerConfig.getNakadiUrl()));
        this.setApplicationName(Optional.ofNullable(this.getApplicationName()).orElse(defaultConsumerConfig.getApplicationName()));

        // oauth
        if(defaultConsumerConfig.getOauth().getEnabled() && !this.getOauth().getEnabled()) {
            this.oauth.setEnabled(defaultConsumerConfig.getOauth().getEnabled());
            this.oauth.setAccessTokenIdIfNotConfigured(defaultConsumerConfig.getOauth().getAccessTokenId());
        }
    }
}
