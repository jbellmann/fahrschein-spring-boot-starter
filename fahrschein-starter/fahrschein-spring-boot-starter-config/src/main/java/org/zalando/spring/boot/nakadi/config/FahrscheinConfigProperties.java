package org.zalando.spring.boot.nakadi.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Validated
@Component
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
            final Errors errors = new BeanPropertyBindingResult(e.getValue(), e.getKey());
            ValidationUtils.invokeValidator(new ConsumerConfigValidator(), e.getValue(), errors);
            if(errors.hasErrors()) {
                log.warn(errors.toString());
                log.warn("Will throw an exception in future");
            }
        });
        publisher.mergeWithDefaultConfig(defaults);
    }
}
