package org.zalando.spring.boot.nakadi.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Component
@Validated
public class FahrscheinConfigProperties {

    @NestedConfigurationProperty
    private DefaultConsumerConfig defaults = new DefaultConsumerConfig();

    private Map<String, ConsumerConfig> consumers = new LinkedHashMap<>();

    private PublisherConfig publisher = new PublisherConfig();

    void postProcess() {
        consumers.entrySet().forEach(e -> {
            e.getValue().mergeWithDefaultConfig(defaults);
            e.getValue().setId(e.getKey());
            e.getValue().getOauth().setAccessTokenIdIfNotConfigured(e.getKey());
        });
        publisher.mergeWithDefaultConfig(defaults);
    }
}
