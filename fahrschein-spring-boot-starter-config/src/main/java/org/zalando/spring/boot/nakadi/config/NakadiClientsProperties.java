package org.zalando.spring.boot.nakadi.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.zalando.spring.boot.config.TimeSpan;

import lombok.Data;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "fahrschein")
public class NakadiClientsProperties {

    private Map<String, Client> clients = new LinkedHashMap<>();

    private boolean loggingSubscriptionEventListenerEnabled = true;

    @Data
    public static final class Client {

        private String accessTokenId;

        @NotBlank
        private String nakadiUri;

        private NakadiConsumerDefaults defaults;

        private Map<String, NakadiConsumerConfig> consumers = new LinkedHashMap<>();

        private List<String> publishers = new LinkedList<>();

        @NestedConfigurationProperty
        private HttpConfigProperties httpConfig = new HttpConfigProperties();

        @Data
        public static final class NakadiConsumerDefaults {

            private boolean autostartEnabled = true;

            private String applicationName;

            private String consumerGroup = "default";

            private Position readFrom = Position.END;

            @NestedConfigurationProperty
            private StreamParametersConfig streamParameters = new StreamParametersConfig();
        }

        @Data
        public static final class NakadiConsumerConfig {

            private boolean autostartEnabled = true;

            private String applicationName;

            private String consumerGroup;

            private List<String> topics = new ArrayList<>();

            private Position readFrom = Position.END;

            @NestedConfigurationProperty
            private StreamParametersConfig streamParameters;
        }
    }

    public enum Position {
        BEGIN("begin"), END("end");

        private final String value;

        Position(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Data
    public static final class StreamParametersConfig {
        private Integer batchLimit;
        private Integer streamLimit;
        private Integer batchFlushTimeout;
        private Integer streamTimeout;
        private Integer streamKeepAliveLimit;

        // Only used in the subscription api
        private Integer maxUncommittedEvents;
    }

    @Data
    public static class HttpConfigProperties {

        private TimeSpan socketTimeout = TimeSpan.of(60, TimeUnit.SECONDS);

        private TimeSpan connectTimeout = TimeSpan.of(2000, TimeUnit.MILLISECONDS);

        private TimeSpan connectionRequestTimeout = TimeSpan.of(8000, TimeUnit.MILLISECONDS);

        private boolean contentCompressionEnabled = false;

        private int bufferSize = 512;

        private TimeSpan connectionTimeToLive = TimeSpan.of(30, TimeUnit.SECONDS);

        private int maxConnectionsTotal = 16;

        private int maxConnectionsPerRoute = 6;

        private boolean evictExpiredConnections = true;

        private boolean evictIdleConnections = true;

        private long maxIdleTime = 10_000;
    }
}
