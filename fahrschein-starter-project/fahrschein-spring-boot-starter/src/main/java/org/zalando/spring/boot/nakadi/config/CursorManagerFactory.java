package org.zalando.spring.boot.nakadi.config;

import java.net.URI;

import org.zalando.fahrschein.CursorManager;
import org.zalando.fahrschein.ManagedCursorManager;
import org.zalando.fahrschein.http.api.RequestFactory;

class CursorManagerFactory {

    static CursorManager create(AbstractConfig config, RequestFactory requestFactory) {
        return new ManagedCursorManager(URI.create(config.getNakadiUrl()), requestFactory);
    }
}
