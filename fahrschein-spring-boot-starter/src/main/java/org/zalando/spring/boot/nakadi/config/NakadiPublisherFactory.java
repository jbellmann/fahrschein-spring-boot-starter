package org.zalando.spring.boot.nakadi.config;

import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.nakadi.NakadiPublisher;

class NakadiPublisherFactory {

    public static NakadiPublisher create(NakadiClient nakadiClient) {
        return new DefaultNakadiPublisher(nakadiClient);
    }

}
