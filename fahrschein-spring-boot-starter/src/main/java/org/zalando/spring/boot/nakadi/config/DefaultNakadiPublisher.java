package org.zalando.spring.boot.nakadi.config;

import java.io.IOException;
import java.util.List;

import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.nakadi.NakadiPublisher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultNakadiPublisher implements NakadiPublisher {
    private final NakadiClient closeableNakadiClient;

    @Override
    public <Type> void publish(String eventName, List<Type> events) {
        try {
            closeableNakadiClient.publish(eventName, events);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
