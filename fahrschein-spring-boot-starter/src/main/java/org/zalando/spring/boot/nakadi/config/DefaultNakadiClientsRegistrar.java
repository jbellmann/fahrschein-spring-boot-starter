package org.zalando.spring.boot.nakadi.config;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import java.io.IOException;

import org.zalando.fahrschein.AccessTokenProvider;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.config.Registry;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.NakadiPublisher;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerConfig;
import org.zalando.stups.tokens.AccessTokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultNakadiClientsRegistrar implements NakadiClientsRegistrar {

    private final Registry registry;
    private final NakadiClientsProperties properties;
    private final AccessTokens accessTokens;

    @Override
    public void register() {
        properties.getClients().forEach((clientId, client) -> {
            String registeredClientId = registerNakadiClient(clientId, client);
            client.getConsumers().forEach((consumerId, listener) -> {
                registerConsumer(registeredClientId, consumerId, listener);
            });
            client.getPublishers().stream().forEach(publisherId -> {
                registerPublisher(registeredClientId, publisherId);
            });
        });
    }

    private String registerNakadiClient(final String id, final Client client) {
        return registry.registerIfAbsent(id, NakadiClient.class, () -> {
            log.info("NakadiClient: [{}] registered", id);
            return genericBeanDefinition(NakadiClientFactory.class)
                .addConstructorArgValue(buildAccessTokenProvider(client.getAccessTokenId()))
                .addConstructorArgValue(client)
                .addConstructorArgValue(id)
                .setFactoryMethod("create");

        });
    }

    private String registerConsumer(final String nakadiClientId, final String listenerId, final NakadiConsumerConfig sub) {
        return registry.registerIfAbsent(listenerId, NakadiConsumer.class, () -> {
            log.info("NakadiConsumer: [{}] registered with NakadiClient: [{}]", listenerId, nakadiClientId);
            return genericBeanDefinition(NakadiConsumerFactory.class)
                    .addConstructorArgReference(nakadiClientId)
                    .addConstructorArgValue(sub)
                    .setFactoryMethod("create");
        });
    }

    private String registerPublisher(final String nakadiClientId, final String publisherId) {
        return registry.registerIfAbsent(publisherId, NakadiPublisher.class, () -> {
            log.info("NakadiPublisher: [{}] registered with NakadiClient: [{}]", publisherId, nakadiClientId);
            return genericBeanDefinition(NakadiPublisherFactory.class)
                    .addConstructorArgReference(nakadiClientId)
                    .setFactoryMethod("create");
        });
    }

    private AccessTokenProvider noopAccessTokenProvider = new AccessTokenProvider() {
        @Override
        public String getAccessToken() throws IOException {
            return "NOOP_ACCESS_TOKEN";
        }
    };;

    protected AccessTokenProvider buildAccessTokenProvider(String accessTokenId) {
        return accessTokenId == null ? noopAccessTokenProvider : new AccessTokenProvider() {
            @Override
            public String getAccessToken() throws IOException {
                return accessTokens.get(accessTokenId);
            }
        };
    }
}
