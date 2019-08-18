package org.zalando.spring.boot.nakadi.config;

import static com.google.common.collect.Sets.newHashSet;
import static org.zalando.fahrschein.AuthorizationBuilder.authorization;
import static org.zalando.fahrschein.domain.Authorization.AuthorizationAttribute.ANYONE;
import static org.zalando.spring.boot.nakadi.config.Position.END;

import java.io.IOException;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.zalando.fahrschein.IORunnable;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.StreamParameters;
import org.zalando.fahrschein.SubscriptionBuilder;
import org.zalando.fahrschein.domain.Subscription;
import org.zalando.spring.boot.nakadi.NakadiListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FahrscheinNakadiConsumer implements NakadiConsumer, BeanNameAware, ApplicationEventPublisherAware {

    private final NakadiClient nakadiClient;
    private final ConsumerConfig consumerConfig;

    private String beanName;
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public <Type> IORunnable runnable(NakadiListener<Type> listener) throws IOException {
        final Subscription sub = getSubscription();
        final StreamParameters streamParams = getStreamParameters();
        final IORunnable result = nakadiClient.stream(sub).withStreamParameters(streamParams)
                .runnable(listener.getEventType(), listener);

        return result;
    }

    private Subscription getSubscription() throws IOException {
        SubscriptionBuilder sb = nakadiClient
                .subscription(consumerConfig.getApplicationName(), newHashSet(consumerConfig.getTopics()))
                .withConsumerGroup(consumerConfig.getConsumerGroup())
                .withAuthorization(authorization()
                        .withReaders(ANYONE)
                        .addAdmin("user", "me")
                        .build());

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
            StreamParametersConfig config = consumerConfig.getStreamParameters();

            StreamParameters sp = new StreamParameters();
            if (config.getBatchFlushTimeout() != null) {
                sp = sp.withBatchFlushTimeout((int) config.getBatchFlushTimeout());
            }

            if (config.getBatchLimit() != null) {
                sp = sp.withBatchLimit((int) config.getBatchLimit());
            }

            if (config.getMaxUncommittedEvents() != null) {
                sp = sp.withMaxUncommittedEvents((int) config.getMaxUncommittedEvents());
            }

            if (config.getStreamKeepAliveLimit() != null) {
                sp = sp.withStreamKeepAliveLimit((int) config.getStreamKeepAliveLimit());
            }

            if (config.getStreamLimit() != null) {
                sp = sp.withStreamLimit((int) config.getStreamLimit());
            }
            if (config.getStreamTimeout() != null) {
                sp = sp.withStreamTimeout((int) config.getStreamTimeout());
            }
            return sp;
        }
    }
}
