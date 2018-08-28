package org.zalando.spring.boot.nakadi.config;

import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerConfig;

class NakadiConsumerFactory {

    public static NakadiConsumer create(NakadiClient client, NakadiConsumerConfig subscription) {
        return new DefaultNakadiConsumer(client, subscription);
    }

}
