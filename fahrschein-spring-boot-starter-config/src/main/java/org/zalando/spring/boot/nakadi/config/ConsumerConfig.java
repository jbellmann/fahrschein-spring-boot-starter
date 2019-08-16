package org.zalando.spring.boot.nakadi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false) 
class ConsumerConfig extends AbstractConfig {

    private StreamParametersConfig streamParameters = new StreamParametersConfig();

    private List<String> topics = new ArrayList<>();

    void mergeWithDefaultConfig(DefaultConsumerConfig defaultConsumerConfig) {
        this.setApplicationName(Optional.ofNullable(this.getApplicationName()).orElse(defaultConsumerConfig.getApplicationName()));
        this.setConsumerGroup(Optional.ofNullable(this.getConsumerGroup()).orElse(defaultConsumerConfig.getConsumerGroup()));
        this.setNakadiUrl(Optional.ofNullable(this.getNakadiUrl()).orElse(defaultConsumerConfig.getNakadiUrl()));
        this.setAutostartEnabled(Optional.ofNullable(this.getAutostartEnabled()).orElse(defaultConsumerConfig.getAutostartEnabled()));
        this.setReadFrom(Optional.ofNullable(this.getReadFrom()).orElse(defaultConsumerConfig.getReadFrom()));

        // oauth
        if(defaultConsumerConfig.getOauth().getEnabled() && !this.getOauth().getEnabled()) {
            this.oauth.setEnabled(defaultConsumerConfig.getOauth().getEnabled());
            this.oauth.setAccessTokenIdIfNotConfigured(defaultConsumerConfig.getOauth().getAccessTokenId());
        }
    }

}