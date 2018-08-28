package org.zalando.spring.boot.nakadi.config;

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;

import org.zalando.fahrschein.IORunnable;
import org.zalando.fahrschein.Listener;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.StreamParameters;
import org.zalando.fahrschein.domain.Subscription;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerConfig;

class DefaultNakadiConsumer implements NakadiConsumer {

    private final NakadiClient nakadiClient;
    private final NakadiConsumerConfig consumerConfig;

    DefaultNakadiConsumer(NakadiClient client, NakadiConsumerConfig consumerConfig) {
        this.nakadiClient = client;
        this.consumerConfig = consumerConfig;
    }

    @Override
    public <Type> void listen(Class<Type> clazz, Listener<Type> listener) {
        try {
            nakadiClient.stream(getSubscription())
            .withStreamParameters(getStreamParameters(consumerConfig))
            .listen(clazz, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Subscription getSubscription() throws IOException {
        return nakadiClient.subscription(consumerConfig.getApplicationName(), newHashSet(consumerConfig.getTopics()))
                            .withConsumerGroup(consumerConfig.getConsumerGroup())
                            .readFromBegin()
                            .subscribe();
    }

    private StreamParameters getStreamParameters(NakadiConsumerConfig config) {
        return new StreamParameters()
                .withBatchFlushTimeout(config.getStreamParameters().getBatchFlushTimeout())
                .withBatchLimit(config.getStreamParameters().getBatchLimit())
                .withMaxUncommittedEvents(config.getStreamParameters().getMaxUncommittedEvents())
                .withStreamKeepAliveLimit(config.getStreamParameters().getStreamKeepAliveLimit())
                .withStreamLimit(config.getStreamParameters().getStreamLimit())
                .withStreamTimeout(config.getStreamParameters().getStreamTimeout());
    }

    @Override
    public <Type> IORunnable runnable(Class<Type> clazz, Listener<Type> listener) throws IOException {
            return nakadiClient.stream(getSubscription())
                    .withStreamParameters(getStreamParameters(consumerConfig))
                    .runnable(clazz, listener);
    }
}
