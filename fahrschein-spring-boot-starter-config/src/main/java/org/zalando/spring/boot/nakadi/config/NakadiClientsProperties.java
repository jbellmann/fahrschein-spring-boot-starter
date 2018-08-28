package org.zalando.spring.boot.nakadi.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.zalando.spring.boot.config.TimeSpan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "fahrschein")
public final class NakadiClientsProperties {

    private Defaults defaults = new Defaults();
    private Map<String, Client> clients = new LinkedHashMap<>();

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class Defaults {
        private String something;

    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class Client {

        private String accessTokenId;

        @NotBlank
        private String nakadiUri = "http://localhost";

        private Map<String, NakadiConsumerConfig> consumers = new LinkedHashMap<>();

        private List<String> publishers = new LinkedList<>();

        @NestedConfigurationProperty
        private RequestConfigProperties requestConfig = new RequestConfigProperties();

        @NestedConfigurationProperty
        private ConnectionConfigProperties connectionConfig = new ConnectionConfigProperties();

        @NestedConfigurationProperty
        private ClientConfigProperties clientConfig = new ClientConfigProperties();

        @Setter
        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static final class NakadiConsumerConfig {
            private String applicationName;
            private String consumerGroup;
            private List<String> topics = new ArrayList<>();
            private String readFrom;
            @NestedConfigurationProperty
            private StreamParametersConfig streamParameters;
        }

        @Setter
        @Getter
        @NoArgsConstructor
        public static final class NakadiPublisherConfig {
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class StreamParametersConfig {
        @Nullable
        private Integer batchLimit;
        @Nullable
        private Integer streamLimit;
        @Nullable
        private Integer batchFlushTimeout;
        @Nullable
        private Integer streamTimeout;
        @Nullable
        private Integer streamKeepAliveLimit;
        // Only used in the subscription api
        @Nullable
        private Integer maxUncommittedEvents;
    }

    @Data
    public static class RequestConfigProperties {
        private TimeSpan socketTimeout = TimeSpan.of(60, TimeUnit.SECONDS);
        private TimeSpan connectTimeout = TimeSpan.of(2000, TimeUnit.MILLISECONDS);
        private TimeSpan connectionRequestTimeout = TimeSpan.of(8000, TimeUnit.MILLISECONDS);
        private boolean contentCompressionEnabled = false;
    }

    @Data
    public static class ConnectionConfigProperties {
        private int bufferSize = 512;
    }

    @Data
    public static class ClientConfigProperties {
        private TimeSpan connectionTimeToLive = TimeSpan.of(30, TimeUnit.SECONDS);
        private int maxConnectionsTotal = 16;
        private int maxConnectionsPerRoute = 6;
    }
}
