package org.zalando.spring.boot.nakadi.config;

import lombok.Data;

@Data
public class ThreadConfig {

    private int listenerPoolSize = 1;

}
