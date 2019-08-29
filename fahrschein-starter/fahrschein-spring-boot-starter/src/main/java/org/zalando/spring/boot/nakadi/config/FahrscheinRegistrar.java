package org.zalando.spring.boot.nakadi.config;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.springframework.core.env.Environment;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.config.Registry;
import org.zalando.spring.boot.nakadi.NakadiPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FahrscheinRegistrar implements NakadiClientsRegistrar {
    
    private static final String LOG_PREFIX = "[{}] - register ";
    
    private final Registry registry;
    private final FahrscheinConfigProperties fahrscheinConfigProperties;
    private final Environment environment;

    @Override
    public void register() {
        fahrscheinConfigProperties.getConsumers().forEach((consumerId, consumerConfig) -> {
            registerNakadiListenerContainer(consumerConfig);
            log.info("[{}] - Consumer registered", consumerConfig.getId());
        });
        registerPublisher(fahrscheinConfigProperties.getPublisher());
    }

    private String registerNakadiListenerContainer(ConsumerConfig consumerConfig) {
        return registry.registerIfAbsent(consumerConfig.getId(), NakadiListenerContainer.class, () -> {
            log.info(LOG_PREFIX + "NakadiListenerContainer ...", consumerConfig.getId());
            return genericBeanDefinition(NakadiListenerContainer.class)
                    .addConstructorArgReference(registerNakadiConsumer(consumerConfig))
                    .addConstructorArgReference(consumerConfig.getId() + "NakadiListener")
                    .addPropertyValue("autoStartup", consumerConfig.getAutostartEnabled());
        });
    }

    private String registerNakadiConsumer(ConsumerConfig consumerConfig) {
        return registry.registerIfAbsent(consumerConfig.getId(), NakadiConsumer.class, () -> {
            log.info(LOG_PREFIX + "NakadiConsumer ...", consumerConfig.getId());
            return genericBeanDefinition(FahrscheinNakadiConsumer.class)
                    .addConstructorArgReference(registerNakadiClient(consumerConfig, "consumer"))
                    .addConstructorArgValue(consumerConfig);
        });
    }

    private String registerPublisher(PublisherConfig publisherConfig) {
        return registry.registerIfAbsent(publisherConfig.getId(), NakadiPublisher.class, () -> {
            log.info(LOG_PREFIX + "NakadiPublisher ...", publisherConfig.getId());
            return genericBeanDefinition(DefaultNakadiPublisher.class)
                    .addConstructorArgReference(registerNakadiClient(publisherConfig, "publisher"));
        });
    }


    private String registerNakadiClient(AbstractConfig consumerConfig, String type) {
        return registry.registerIfAbsent(consumerConfig.getId() + "-" + type, NakadiClient.class, () -> {
            log.info(LOG_PREFIX + "NakadiClient ...", consumerConfig.getId());
            return genericBeanDefinition(FahrscheinNakadiClientFactory.class)
                .addConstructorArgReference("fahrscheinAccessTokenProvider")
                .addConstructorArgValue(consumerConfig)
                .setFactoryMethod("create");
        });
    }
}
