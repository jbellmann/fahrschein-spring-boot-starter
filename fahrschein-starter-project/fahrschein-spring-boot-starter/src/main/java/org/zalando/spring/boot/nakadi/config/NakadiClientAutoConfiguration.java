package org.zalando.spring.boot.nakadi.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@Configuration
@AutoConfigureAfter(name = { 
        "org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration"
})
public class NakadiClientAutoConfiguration {

    @Bean
    public static NakadiClientsPostProcessor nakadiClientPostProcessor() {
        return new NakadiClientsPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean({MeterRegistry.class})
    public static MeterRegistry consoleLoggingRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    @ConditionalOnBean({MeterRegistry.class})
    public static BeanPostProcessor meterRegistryAwareBeanPostProcessor() {
        return new MeterRegistryAwareBeanPostProcessor();
    }
}
