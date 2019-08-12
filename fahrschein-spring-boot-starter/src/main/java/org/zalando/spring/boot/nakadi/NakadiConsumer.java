package org.zalando.spring.boot.nakadi;

import java.io.IOException;

import org.zalando.fahrschein.IORunnable;

public interface NakadiConsumer {

    <Type> IORunnable runnable(NakadiListener<Type> listener) throws IOException;

}