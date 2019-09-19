package org.zalando.spring.boot.nakadi.config.properties;

import java.util.concurrent.TimeUnit;

import org.zalando.spring.boot.config.TimeSpan;

import lombok.Data;

@Data
public class HttpConfig {

    private TimeSpan socketTimeout;

    private TimeSpan connectTimeout;

    private TimeSpan connectionRequestTimeout;

    private Boolean contentCompressionEnabled;

    private Integer bufferSize;

    private TimeSpan connectionTimeToLive;

    private Integer maxConnectionsTotal;

    private Integer maxConnectionsPerRoute;

    private Boolean evictExpiredConnections;

    private Boolean evictIdleConnections;

    private Long maxIdleTime;

    public static HttpConfig defaultHttpConfig() {
        HttpConfig config = new HttpConfig();
        config.setSocketTimeout(TimeSpan.of(60, TimeUnit.SECONDS));
        config.setConnectTimeout(TimeSpan.of(2000, TimeUnit.MILLISECONDS));
        config.setConnectionRequestTimeout(TimeSpan.of(8000, TimeUnit.MILLISECONDS));
        config.setContentCompressionEnabled(false);
        config.setBufferSize(512);
        config.setConnectionTimeToLive(TimeSpan.of(30, TimeUnit.SECONDS));
        config.setMaxConnectionsTotal(3);
        config.setMaxConnectionsPerRoute(3);
        config.setEvictExpiredConnections(true);
        config.setEvictIdleConnections(true);
        config.setMaxIdleTime(Long.valueOf(10_000));
        return config;
    }

}
