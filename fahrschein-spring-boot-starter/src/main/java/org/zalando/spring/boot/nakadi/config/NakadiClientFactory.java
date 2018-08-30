package org.zalando.spring.boot.nakadi.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.URI;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.zalando.fahrschein.AccessTokenProvider;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.NakadiClientBuilder;
import org.zalando.fahrschein.http.apache.HttpComponentsRequestFactory;
import org.zalando.fahrschein.http.api.RequestFactory;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PROTECTED)
class NakadiClientFactory {

    public static NakadiClient create(AccessTokenProvider accessTokenProvider, Client client, String clientId) {
        return nakadiEmployeeRecordEventsClient(accessTokenProvider, client, clientId);
    }

    protected static NakadiClient nakadiEmployeeRecordEventsClient(AccessTokenProvider accessTokenProvider, Client client, String clientId) {

        final ObjectMapper objectMapper = buildObjectMapper();

        NakadiClientBuilder ncb = NakadiClient.builder(URI.create(client.getNakadiUri()))
                                            .withRequestFactory(buildRequestFactory(client))
                                            .withObjectMapper(objectMapper);

                                            if (client.getAccessTokenId() != null) {
                                                ncb = ncb.withAccessTokenProvider(accessTokenProvider);
                                            } else {
                                                log.info("NakadiClient: [{}] - No AccessTokenProvider configured. No 'accessTokenId' was set.", clientId);
                                            }

        return ncb.build();
    }

    protected static RequestFactory buildRequestFactory(Client client) {
        final RequestConfig config = RequestConfig.custom()
                .setSocketTimeout((int) MILLISECONDS.convert(client.getHttpConfig().getSocketTimeout().getAmount(), client.getHttpConfig().getSocketTimeout().getUnit()))
                .setConnectTimeout((int) MILLISECONDS.convert(client.getHttpConfig().getConnectTimeout().getAmount(), client.getHttpConfig().getConnectTimeout().getUnit()))
                .setConnectionRequestTimeout((int) MILLISECONDS.convert(client.getHttpConfig().getConnectionRequestTimeout().getAmount(), client.getHttpConfig().getConnectionRequestTimeout().getUnit()))
                .build();

    final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setBufferSize(client.getHttpConfig().getBufferSize())
                .build();

    final CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setDefaultConnectionConfig(connectionConfig)
                .setConnectionTimeToLive(client.getHttpConfig().getConnectionTimeToLive().getAmount(), client.getHttpConfig().getConnectionTimeToLive().getUnit())
                .disableAutomaticRetries()
                .disableRedirectHandling()
                .setMaxConnTotal(client.getHttpConfig().getMaxConnectionsTotal())
                .setMaxConnPerRoute(client.getHttpConfig().getMaxConnectionsPerRoute())
                .build();

        return new HttpComponentsRequestFactory(httpClient);
    }

    private static ObjectMapper buildObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    objectMapper.registerModules(new Jdk8Module(), new ParameterNamesModule(), new JavaTimeModule());
                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
