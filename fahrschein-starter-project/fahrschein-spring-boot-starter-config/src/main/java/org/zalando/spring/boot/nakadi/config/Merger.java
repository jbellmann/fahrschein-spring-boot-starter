package org.zalando.spring.boot.nakadi.config;

import java.util.Arrays;
import java.util.Objects;

final class Merger {

    @SafeVarargs
    static <T> T merge(T... options) {
        return Arrays.stream(options).filter(Objects::nonNull).findFirst().orElse(null);
    }

}
