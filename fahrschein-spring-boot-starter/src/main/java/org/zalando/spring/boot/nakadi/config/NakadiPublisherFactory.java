package org.zalando.spring.boot.nakadi.config;

import org.zalando.spring.boot.nakadi.CloseableNakadiClient;
import org.zalando.spring.boot.nakadi.NakadiPublisher;

class NakadiPublisherFactory {

    public static NakadiPublisher create(CloseableNakadiClient nakadiClient) {
        return new DefaultNakadiPublisher(nakadiClient);
    }

}
