package com.demo.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry(final MetricRegistry metricRegistry, final Clock clock) {
        final DropwizardMeterRegistry registry = new DropwizardMeterRegistry(new DropwizardConfig() {
            @Override
            public String prefix() {
                return "foobar";
            }

            @Override
            public String get(final String key) {
                return null;
            }
        }, metricRegistry, HierarchicalNameMapper.DEFAULT, clock) {
            @Override
            protected Double nullGaugeValue() {
                return null;
            }
        };

        registry.config().namingConvention(NamingConvention.identity);
        return registry;
    }

}
