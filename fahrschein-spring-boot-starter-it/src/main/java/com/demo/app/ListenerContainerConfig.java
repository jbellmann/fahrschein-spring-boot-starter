package com.demo.app;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.support.NakadiListenerContainer;
import org.zalando.spring.boot.nakadi.support.NakadiListenerSupportConfig;

@Configuration
@ImportAutoConfiguration({ NakadiListenerSupportConfig.class })
public class ListenerContainerConfig {

    @Autowired
    @Qualifier(NakadiListenerSupportConfig.NAKADI_LISTENER_EXECUTOR_SERVICE)
    private ExecutorService executorService;

    @Autowired
    @Qualifier("example")
    private NakadiConsumer outfitUpdateConsumer;

    @Bean
    public NakadiListenerContainer<OutfitUpdateEvent> outfitUpdateListenerContainer() {
        return new NakadiListenerContainer<OutfitUpdateEvent>(executorService, outfitUpdateConsumer,
                OutfitUpdateEvent.class, new OutfitUpdateEventListener());
    }
}
