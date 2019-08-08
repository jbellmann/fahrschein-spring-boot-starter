package org.zalando.spring.boot.nakadi;

import org.springframework.context.SmartLifecycle;
import org.zalando.fahrschein.NakadiClient;

public interface CloseableNakadiClient extends SmartLifecycle {

    NakadiClient getDelegate();
}
