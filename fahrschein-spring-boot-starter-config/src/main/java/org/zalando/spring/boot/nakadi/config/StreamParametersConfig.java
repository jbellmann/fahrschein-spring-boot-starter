package org.zalando.spring.boot.nakadi.config;

import lombok.Data;

@Data
public class StreamParametersConfig {
    private Integer batchLimit;
    private Integer streamLimit;
    private Integer batchFlushTimeout;
    private Integer streamTimeout;
    private Integer streamKeepAliveLimit;

    // Only used in the subscription api
    private Integer maxUncommittedEvents;

}
