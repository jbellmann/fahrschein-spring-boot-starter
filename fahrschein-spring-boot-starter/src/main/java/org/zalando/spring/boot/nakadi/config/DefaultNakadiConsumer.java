package org.zalando.spring.boot.nakadi.config;

import static com.google.common.collect.Sets.newHashSet;
import static org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Position.END;

import java.io.IOException;

import org.zalando.fahrschein.IORunnable;
import org.zalando.fahrschein.Listener;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.StreamParameters;
import org.zalando.fahrschein.SubscriptionBuilder;
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
            .withStreamParameters(getStreamParameters())
            .listen(clazz, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Subscription getSubscription() throws IOException {
        SubscriptionBuilder sb = nakadiClient.subscription(consumerConfig.getApplicationName(), newHashSet(consumerConfig.getTopics()))
                            .withConsumerGroup(consumerConfig.getConsumerGroup());

        if (END.equals(consumerConfig.getReadFrom())) {
            sb = sb.readFromEnd();
        } else {
            sb = sb.readFromBegin();
        }
        return sb.subscribe();
    }

    protected StreamParameters getStreamParameters() {
        if (consumerConfig.getStreamParameters() == null) {
            return new StreamParameters();
        } else {
            StreamParameters sp = new StreamParameters();
            if (consumerConfig.getStreamParameters().getBatchFlushTimeout() != null) {
                sp = sp.withBatchFlushTimeout((int) consumerConfig.getStreamParameters().getBatchFlushTimeout());
            }

            if (consumerConfig.getStreamParameters().getBatchLimit() != null) {
                sp = sp.withBatchLimit((int) consumerConfig.getStreamParameters().getBatchLimit());
            }

            if (consumerConfig.getStreamParameters().getMaxUncommittedEvents() != null) {
                sp = sp.withMaxUncommittedEvents((int) consumerConfig.getStreamParameters().getMaxUncommittedEvents());
            }

            if (consumerConfig.getStreamParameters().getStreamKeepAliveLimit() != null) {
                sp = sp.withStreamKeepAliveLimit((int) consumerConfig.getStreamParameters().getStreamKeepAliveLimit());
            }

            if (consumerConfig.getStreamParameters().getStreamLimit() != null) {
                sp = sp.withStreamLimit((int) consumerConfig.getStreamParameters().getStreamLimit());
            }
            if (consumerConfig.getStreamParameters().getStreamTimeout() != null) {
                sp = sp.withStreamTimeout((int) consumerConfig.getStreamParameters().getStreamTimeout());
            }
            return sp;
        }
    }

    @Override
    public <Type> IORunnable runnable(Class<Type> clazz, Listener<Type> listener) throws IOException {
            return nakadiClient.stream(getSubscription())
                    .withStreamParameters(getStreamParameters())
                    .runnable(clazz, listener);
    }
}
