package org.zalando.spring.boot.nakadi.config;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import java.io.IOException;

import org.springframework.core.env.Environment;
import org.zalando.fahrschein.AccessTokenProvider;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.config.Registry;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.NakadiPublisher;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerConfig;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerDefaults;
import org.zalando.stups.tokens.AccessTokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultNakadiClientsRegistrar implements NakadiClientsRegistrar {

    private final Registry registry;
    private final NakadiClientsProperties properties;
    private final AccessTokens accessTokens;
    private final Environment environment;

    @Override
    public void register() {
        properties.getClients().forEach((clientId, client) -> {
            String registeredClientId = registerNakadiClient(clientId, client);
            client.getConsumers().forEach((consumerId, nakadiConsumerConfig) -> {
                final String consumerIdentifier = registerConsumer(registeredClientId, consumerId, nakadiConsumerConfig, client.getDefaults());
                final String nakadiListenerContainerId = registerNakadiListenerContainer(consumerIdentifier, consumerId, nakadiConsumerConfig);
                log.info("Registered consumer : {} and listenerContainer : {}", consumerIdentifier, nakadiListenerContainerId);
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

    private String registerConsumer(final String nakadiClientId, final String listenerId, final NakadiConsumerConfig consumerConfig, NakadiConsumerDefaults consumerDefaults) {
        return registry.registerIfAbsent(listenerId, NakadiConsumer.class, () -> {
            log.info("NakadiConsumer: [{}] registered with NakadiClient: [{}]", listenerId, nakadiClientId);
            return genericBeanDefinition(DefaultNakadiConsumer.class)
                    .addConstructorArgReference(nakadiClientId)
                    .addConstructorArgValue(consumerConfig)
                    .addConstructorArgValue(consumerDefaults);
        });
    }

    private String registerNakadiListenerContainer(final String nakadiConsumerId, final String listenerPrefix, final NakadiConsumerConfig consumerConfig) {
        return registry.registerIfAbsent(listenerPrefix, NakadiListenerContainer.class, () -> {
            log.info("NakadiListenerContainer: [{}] registered with NakadiConsumer: [{}]", listenerPrefix + "NakadiListenerContainer", nakadiConsumerId);
            return genericBeanDefinition(NakadiListenerContainer.class)
                    .addConstructorArgReference(nakadiConsumerId)
                    .addConstructorArgReference(listenerPrefix + "NakadiListener")

                    .addPropertyValue("autoStartup", this.properties.getGlobal().isAutostartEnabled() && consumerConfig.isAutostartEnabled());
        });
    }

    private String registerPublisher(final String nakadiClientId, final String publisherId) {
        return registry.registerIfAbsent(publisherId, NakadiPublisher.class, () -> {
            log.info("NakadiPublisher: [{}] registered with NakadiClient: [{}]", publisherId, nakadiClientId);
            return genericBeanDefinition(DefaultNakadiPublisher.class)
                    .addConstructorArgReference(nakadiClientId);
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
