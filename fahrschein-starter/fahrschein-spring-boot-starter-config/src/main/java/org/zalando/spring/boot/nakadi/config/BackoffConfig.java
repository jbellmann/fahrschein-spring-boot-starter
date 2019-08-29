package org.zalando.spring.boot.nakadi.config;

import java.util.concurrent.TimeUnit;

import org.zalando.spring.boot.config.TimeSpan;

import lombok.Data;

@Data
public class BackoffConfig {

    private Boolean enabled;

    private TimeSpan initialDelay;

    private TimeSpan maxDelay;

    private Double backoffFactor;

    private Integer maxRetries;

    private JitterConfig jitter;

    public static BackoffConfig defaultBackoffConfig() {
        BackoffConfig c = new BackoffConfig();
        c.setEnabled(Boolean.FALSE);
        c.setInitialDelay(TimeSpan.of(500, TimeUnit.MILLISECONDS));
        c.setMaxDelay(TimeSpan.of(10, TimeUnit.MINUTES));
        c.setBackoffFactor(1.5);
        c.setMaxRetries(-1);
        c.setJitter(new JitterConfig(Boolean.FALSE, JitterType.EQUAL));
        return c;
    }

    public void mergeFromDefaults(BackoffConfig defaultBackoffConfig) {
        if(defaultBackoffConfig.getEnabled()) {
            setEnabled(Boolean.TRUE);
        }
    }
}
