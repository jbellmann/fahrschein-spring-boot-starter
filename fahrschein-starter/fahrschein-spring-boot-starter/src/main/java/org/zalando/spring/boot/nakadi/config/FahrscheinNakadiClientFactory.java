package org.zalando.spring.boot.nakadi.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.zalando.fahrschein.AccessTokenProvider;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.NakadiClientBuilder;
import org.zalando.fahrschein.http.apache.HttpComponentsRequestFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FahrscheinNakadiClientFactory {

    public static NakadiClient create(AccessTokenProvider accessTokenProvider, AbstractConfig config) {
        final ObjectMapper objectMapper = buildObjectMapper();

        CloseableHttpClient closeableHttpClient = buildCloseableHttpClient(config.getHttp());
        NakadiClientBuilder ncb = NakadiClient.builder(URI.create(config.getNakadiUrl()))
                                            .withRequestFactory(new HttpComponentsRequestFactory(closeableHttpClient))
                                            .withObjectMapper(objectMapper);

                                            if (config.getOauth().getEnabled()) {
                                                ncb = ncb.withAccessTokenProvider(accessTokenProvider);
                                            } else {
                                                log.info("NakadiClient: [{}] - No AccessTokenProvider configured. No 'accessTokenId' was set.", config.getId());
                                            }

        return ncb.build();
    }

    protected static CloseableHttpClient buildCloseableHttpClient(HttpConfig httpConfig) {
        final RequestConfig config = RequestConfig.custom()
                .setSocketTimeout((int) MILLISECONDS.convert(httpConfig.getSocketTimeout().getAmount(), httpConfig.getSocketTimeout().getUnit()))
                .setConnectTimeout((int) MILLISECONDS.convert(httpConfig.getConnectTimeout().getAmount(), httpConfig.getConnectTimeout().getUnit()))
                .setConnectionRequestTimeout((int) MILLISECONDS.convert(httpConfig.getConnectionRequestTimeout().getAmount(), httpConfig.getConnectionRequestTimeout().getUnit()))
                .build();

    final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setBufferSize(httpConfig.getBufferSize())
                .build();

    HttpClientBuilder builder = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setDefaultConnectionConfig(connectionConfig)
                .setConnectionTimeToLive(httpConfig.getConnectionTimeToLive().getAmount(), httpConfig.getConnectionTimeToLive().getUnit())
                .disableAutomaticRetries()
                .disableRedirectHandling()
                .setMaxConnTotal(httpConfig.getMaxConnectionsTotal())
                .setMaxConnPerRoute(httpConfig.getMaxConnectionsPerRoute());

                if (httpConfig.getEvictExpiredConnections()) {
                    builder = builder.evictExpiredConnections();
                }

                if (httpConfig.getEvictIdleConnections()) {
                    builder = builder.evictIdleConnections(httpConfig.getMaxIdleTime().longValue(), TimeUnit.MILLISECONDS);
                }

        return builder.build();

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
