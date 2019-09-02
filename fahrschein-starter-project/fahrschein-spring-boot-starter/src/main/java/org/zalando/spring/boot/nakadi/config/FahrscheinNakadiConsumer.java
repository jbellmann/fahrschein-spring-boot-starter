package org.zalando.spring.boot.nakadi.config;

import static com.google.common.collect.Sets.newHashSet;
import static org.zalando.fahrschein.AuthorizationBuilder.authorization;
import static org.zalando.spring.boot.nakadi.config.Position.END;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.zalando.fahrschein.BackoffStrategy;
import org.zalando.fahrschein.EqualJitterBackoffStrategy;
import org.zalando.fahrschein.ExponentialBackoffStrategy;
import org.zalando.fahrschein.FullJitterBackoffStrategy;
import org.zalando.fahrschein.IORunnable;
import org.zalando.fahrschein.MetricsCollector;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.fahrschein.NoBackoffStrategy;
import org.zalando.fahrschein.NoMetricsCollector;
import org.zalando.fahrschein.StreamParameters;
import org.zalando.fahrschein.SubscriptionBuilder;
import org.zalando.fahrschein.domain.Authorization.AuthorizationAttribute;
import org.zalando.fahrschein.domain.Subscription;
import org.zalando.spring.boot.nakadi.MeterRegistryAware;
import org.zalando.spring.boot.nakadi.NakadiListener;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FahrscheinNakadiConsumer implements NakadiConsumer, MeterRegistryAware {

    @NonNull
    private final NakadiClient nakadiClient;

    @NonNull
    private final ConsumerConfig consumerConfig;

    private MeterRegistry meterRegistry;

    @Override
    public ConsumerConfig getConsumerConfig() {
        return this.consumerConfig;
    }

    @Override
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public <Type> IORunnable runnable(NakadiListener<Type> listener) throws IOException {
        final Subscription sub = getSubscription();
        final StreamParameters streamParams = getStreamParameters();
        final IORunnable result = nakadiClient.stream(sub)
                                                .withStreamParameters(streamParams)
                                                .withBackoffStrategy(getBackoffStrategy(consumerConfig))
                                                .withMetricsCollector(getMetricsCollector(consumerConfig, meterRegistry))
                                                .runnable(listener.getEventType(), listener);

        return result;
    }

    protected static MetricsCollector getMetricsCollector(ConsumerConfig consumerConfig, MeterRegistry meterRegistry) {
        if (meterRegistry != null && consumerConfig.getRecordMetrics()) {
            return new MicrometerMetricsCollector(meterRegistry, consumerConfig.getId());
        } else {
            return new NoMetricsCollector();
        }
    }

    protected static BackoffStrategy getBackoffStrategy(ConsumerConfig consumerConfig) {
        if(consumerConfig.getBackoff().getEnabled()) {
            final BackoffConfig c = consumerConfig.getBackoff();
            if(c.getJitter().getEnabled() && c.getJitter().getType().equals(JitterType.EQUAL)) {
                return new EqualJitterBackoffStrategy((int)c.getInitialDelay().getUnit().toMillis(c.getInitialDelay().getAmount()), c.getBackoffFactor(), c.getMaxDelay().getUnit().toMillis(c.getMaxDelay().getAmount()), c.getMaxRetries());
            }
            if(c.getJitter().getEnabled() && c.getJitter().getType().equals(JitterType.FULL)) {
                return new FullJitterBackoffStrategy((int)c.getInitialDelay().getUnit().toMillis(c.getInitialDelay().getAmount()), c.getBackoffFactor(), c.getMaxDelay().getUnit().toMillis(c.getMaxDelay().getAmount()), c.getMaxRetries());
            }
            return new ExponentialBackoffStrategy((int)c.getInitialDelay().getUnit().toMillis(c.getInitialDelay().getAmount()), c.getBackoffFactor(), c.getMaxDelay().getUnit().toMillis(c.getMaxDelay().getAmount()), c.getMaxRetries());
        }
        return new NoBackoffStrategy();
    }

    private Subscription getSubscription() throws IOException {

        List<AuthorizationAttribute> adminAttributes = new LinkedList<>();
        consumerConfig.getAuthorizations().getAdmins()
                .forEach((key, value) -> adminAttributes.add(new AuthorizationAttribute(key, value)));

        List<AuthorizationAttribute> readerAttributes = new LinkedList<>();
        consumerConfig.getAuthorizations().getReaders()
                .forEach((key, value) -> readerAttributes.add(new AuthorizationAttribute(key, value)));

        if (consumerConfig.getAuthorizations().getAnyReader()) {
            readerAttributes.add(AuthorizationAttribute.ANYONE);
        }

        SubscriptionBuilder sb = nakadiClient
                .subscription(consumerConfig.getApplicationName(), newHashSet(consumerConfig.getTopics()))
                .withConsumerGroup(consumerConfig.getConsumerGroup())
                .withAuthorization(authorization()
                        .withAdmins(adminAttributes)
                        .withReaders(readerAttributes)
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
