package org.zalando.spring.boot.nakadi;

import java.util.List;

public interface NakadiPublisher {

    <Type> void publish(String eventName, List<Type> events);

}
