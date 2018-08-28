package org.zalando.spring.boot.nakadi.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.URI;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.zalando.fahrschein.AccessTokenProvider;
import org.zalando.fahrschein.NakadiClient;
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

import lombok.experimental.UtilityClass;

@UtilityClass
class NakadiClientFactory {

    public static NakadiClient create(AccessTokenProvider accessTokenProvider, Client client) {
        return nakadiEmployeeRecordEventsClient(accessTokenProvider, client);
    }

    protected static NakadiClient nakadiEmployeeRecordEventsClient(AccessTokenProvider accessTokenProvider, Client client) {

        final ObjectMapper objectMapper = buildObjectMapper();

        final NakadiClient nakadiClient = NakadiClient.builder(URI.create(client.getNakadiUri()))
                    .withRequestFactory(buildRequestFactory(client))
                    .withAccessTokenProvider(accessTokenProvider)
                    .withObjectMapper(objectMapper)
                    .build();

        return nakadiClient;
    }

    protected static RequestFactory buildRequestFactory(Client client) {
        final RequestConfig config = RequestConfig.custom()
                .setSocketTimeout((int) MILLISECONDS.convert(client.getRequestConfig().getSocketTimeout().getAmount(), client.getRequestConfig().getSocketTimeout().getUnit()))
                .setConnectTimeout((int) MILLISECONDS.convert(client.getRequestConfig().getConnectTimeout().getAmount(), client.getRequestConfig().getConnectTimeout().getUnit()))
                .setConnectionRequestTimeout((int) MILLISECONDS.convert(client.getRequestConfig().getConnectionRequestTimeout().getAmount(), client.getRequestConfig().getConnectionRequestTimeout().getUnit()))
                .build();

    final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setBufferSize(client.getConnectionConfig().getBufferSize())
                .build();

    final CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setDefaultConnectionConfig(connectionConfig)
                .setConnectionTimeToLive(client.getClientConfig().getConnectionTimeToLive().getAmount(), client.getClientConfig().getConnectionTimeToLive().getUnit())
                .disableAutomaticRetries()
                .disableRedirectHandling()
                .setMaxConnTotal(client.getClientConfig().getMaxConnectionsTotal())
                .setMaxConnPerRoute(client.getClientConfig().getMaxConnectionsPerRoute())
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
