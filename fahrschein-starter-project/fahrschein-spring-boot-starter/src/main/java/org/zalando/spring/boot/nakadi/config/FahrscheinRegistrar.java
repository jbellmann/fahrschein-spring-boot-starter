package org.zalando.spring.boot.nakadi.config;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.springframework.core.env.Environment;
import org.zalando.fahrschein.CursorManager;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.http.api.RequestFactory;
import org.zalando.spring.boot.config.Registry;
import org.zalando.spring.boot.nakadi.NakadiPublisher;
import org.zalando.spring.boot.nakadi.config.properties.AbstractConfig;
import org.zalando.spring.boot.nakadi.config.properties.ConsumerConfig;
import org.zalando.spring.boot.nakadi.config.properties.FahrscheinConfigProperties;
import org.zalando.spring.boot.nakadi.config.properties.PublisherConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FahrscheinRegistrar implements NakadiClientsRegistrar {

    private static final String CREATE = "create";

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
            final String requestFactoryRef = registerRequestFactory(consumerConfig);
            return genericBeanDefinition(FahrscheinNakadiClientFactory.class)
                .addConstructorArgReference("fahrscheinAccessTokenProvider")
                .addConstructorArgValue(consumerConfig)
                .addConstructorArgReference(registerCursorManager(consumerConfig, requestFactoryRef))
                .addConstructorArgReference(registerObjectMapper(consumerConfig))
                .addConstructorArgReference(requestFactoryRef)
                .setFactoryMethod(CREATE);
        });
    }

    private String registerRequestFactory(AbstractConfig consumerConfig) {
        return registry.registerIfAbsent(consumerConfig.getId(), RequestFactory.class, () -> {
            log.info(LOG_PREFIX + "RequestFactory ...", consumerConfig.getId());
            return genericBeanDefinition(RequestFactoryFactory.class)
                .addConstructorArgValue(consumerConfig)
                .setFactoryMethod(CREATE);
        });
    }

    private String registerObjectMapper(AbstractConfig consumerConfig) {
        return registry.registerIfAbsent(consumerConfig.getId(), ObjectMapper.class, () -> {
            log.info(LOG_PREFIX + "ObjectMapper ...", consumerConfig.getId());
            return genericBeanDefinition(ObjectMapperFactory.class)
                .setFactoryMethod(CREATE);
        });
    }

    private String registerCursorManager(AbstractConfig consumerConfig, String requestFactoryRef) {
        return registry.registerIfAbsent("",  CursorManager.class, () -> {
            log.info(LOG_PREFIX + "CursorManager ...", consumerConfig.getId());
            return genericBeanDefinition(CursorManagerFactory.class)
                .addConstructorArgValue(consumerConfig)
                .addConstructorArgReference(requestFactoryRef)
                .setFactoryMethod(CREATE);
        });
    }
}
