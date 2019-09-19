package org.zalando.spring.boot.nakadi.config;

import java.net.URI;

import org.zalando.fahrschein.AccessTokenProvider;
import org.zalando.fahrschein.CursorManager;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.NakadiClientBuilder;
import org.zalando.fahrschein.http.api.RequestFactory;
import org.zalando.spring.boot.nakadi.config.properties.AbstractConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class FahrscheinNakadiClientFactory {

    static NakadiClient create(AccessTokenProvider accessTokenProvider, AbstractConfig config, CursorManager cursorManager, ObjectMapper objectMapper, RequestFactory requestFactory) {

        NakadiClientBuilder ncb = NakadiClient.builder(URI.create(config.getNakadiUrl()))
                                            .withRequestFactory(requestFactory)
                                            .withCursorManager(cursorManager)
                                            .withObjectMapper(objectMapper);

                                            if (config.getOauth().getEnabled()) {
                                                ncb = ncb.withAccessTokenProvider(accessTokenProvider);
                                            } else {
                                                log.info("NakadiClient: [{}] - No AccessTokenProvider configured. No 'accessTokenId' was set.", config.getId());
                                            }

        return ncb.build();
    }
}
