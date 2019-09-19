package org.zalando.spring.boot.nakadi.config;

import java.io.IOException;

import org.zalando.fahrschein.IORunnable;
import org.zalando.spring.boot.nakadi.NakadiListener;
import org.zalando.spring.boot.nakadi.config.properties.ConsumerConfig;

public interface NakadiConsumer {

    <Type> IORunnable runnable(NakadiListener<Type> listener) throws IOException;

    ConsumerConfig getConsumerConfig();

}