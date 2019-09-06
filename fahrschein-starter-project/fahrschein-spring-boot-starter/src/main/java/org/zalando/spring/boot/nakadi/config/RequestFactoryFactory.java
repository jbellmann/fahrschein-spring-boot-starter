package org.zalando.spring.boot.nakadi.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.zalando.fahrschein.http.apache.HttpComponentsRequestFactory;
import org.zalando.fahrschein.http.api.RequestFactory;

class RequestFactoryFactory {

    static RequestFactory create(AbstractConfig config) {
        CloseableHttpClient closeableHttpClient = buildCloseableHttpClient(config.getHttp());
        return new HttpComponentsRequestFactory(closeableHttpClient);
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
}
