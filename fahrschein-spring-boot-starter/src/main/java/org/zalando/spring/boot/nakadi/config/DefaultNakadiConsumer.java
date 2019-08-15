package org.zalando.spring.boot.nakadi.config;

import static com.google.common.collect.Sets.newHashSet;
import static org.springframework.util.StringUtils.hasText;
import static org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Position.END;

import java.io.IOException;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.zalando.fahrschein.IORunnable;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.StreamParameters;
import org.zalando.fahrschein.SubscriptionBuilder;
import org.zalando.fahrschein.domain.Subscription;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.NakadiListener;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerConfig;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerDefaults;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.StreamParametersConfig;

class DefaultNakadiConsumer implements NakadiConsumer, BeanNameAware, ApplicationEventPublisherAware {

    private final NakadiClient nakadiClient;
    private final NakadiConsumerConfig consumerConfig;
    private final NakadiConsumerDefaults consumerDefaults;

    private String beanName;
    private ApplicationEventPublisher eventPublisher;

    DefaultNakadiConsumer(NakadiClient client, NakadiConsumerConfig consumerConfig,
            NakadiConsumerDefaults consumerDefaults) {
        this.nakadiClient = client;
        this.consumerConfig = consumerConfig;
        this.consumerDefaults = consumerDefaults;
    }

    private Subscription getSubscription() throws IOException {
        SubscriptionBuilder sb = nakadiClient
                .subscription(getApplicationName(), newHashSet(consumerConfig.getTopics()))
                .withConsumerGroup(getConsumerGroup());

        if (END.equals(consumerConfig.getReadFrom())) {
            sb = sb.readFromEnd();
        } else {
            sb = sb.readFromBegin();
        }
        return sb.subscribe();
    }

    protected StreamParameters getStreamParameters() {
        if (consumerConfig.getStreamParameters() == null && consumerDefaults.getStreamParameters() == null) {
            return new StreamParameters();
        } else {
            StreamParametersConfig config = consumerConfig.getStreamParameters();
            if (config == null) {
                config = consumerDefaults.getStreamParameters();
            }

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

    @Override
    public <Type> IORunnable runnable(NakadiListener<Type> listener) throws IOException {
        final Subscription sub = getSubscription();
        final StreamParameters streamParams = getStreamParameters();
        final IORunnable result = nakadiClient.stream(sub).withStreamParameters(streamParams)
                .runnable(listener.getEventType(), listener);
        return result;
    }

    protected String getApplicationName() {
        return getValueOrDefaultElseThrow(consumerConfig.getApplicationName(), consumerDefaults.getApplicationName(),
                new RuntimeException("'applicationName' is required"));
    }

    protected String getConsumerGroup() {
        return getValueOrDefaultElseThrow(consumerConfig.getConsumerGroup(), consumerDefaults.getConsumerGroup(),
                new RuntimeException("'consumerGroup' is required"));
    }

    protected String getValueOrDefaultElseThrow(String value, String defaultValue, RuntimeException t) {
        if (hasText(value)) {
            return value;
        } else {
            if (hasText(defaultValue)) {
                return defaultValue;
            } else {
                throw t;
            }
        }
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
