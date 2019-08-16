package org.zalando.spring.boot.nakadi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
class DefaultConsumerConfig extends AbstractConfig {

    private StreamParametersConfig streamParameters = new StreamParametersConfig();

    DefaultConsumerConfig() {
        super();
        setId("CONFIG_DEFAULT");
        setAutostartEnabled(Boolean.TRUE);
        setReadFrom(Position.END);
    }

}
