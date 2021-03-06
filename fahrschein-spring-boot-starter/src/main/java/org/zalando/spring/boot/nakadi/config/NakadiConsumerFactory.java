package org.zalando.spring.boot.nakadi.config;

import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerConfig;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerDefaults;

class NakadiConsumerFactory {

    public static NakadiConsumer create(NakadiClient client, NakadiConsumerConfig consumerConfig, NakadiConsumerDefaults consumerDefaults) {
        return new DefaultNakadiConsumer(client, consumerConfig, consumerDefaults);
    }

}
