package org.zalando.spring.boot.nakadi.config.properties;

import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Validated
@EqualsAndHashCode(callSuper = false)
public class DefaultConsumerConfig extends AbstractConfig {

    private StreamParametersConfig streamParameters = new StreamParametersConfig();

    public DefaultConsumerConfig() {
        super();
        setId("CONFIG_DEFAULT");
        setAutostartEnabled(Boolean.TRUE);
        setReadFrom(Position.END);
        setRecordMetrics(Boolean.FALSE);
    }

}
