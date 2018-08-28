package org.zalando.spring.boot.nakadi;

import java.io.IOException;

import org.zalando.fahrschein.IORunnable;
import org.zalando.fahrschein.Listener;

public interface NakadiConsumer {

    <Type> void listen(Class<Type> clazz, Listener<Type> listener);

    <Type> IORunnable runnable(Class<Type> clazz, Listener<Type> listener) throws IOException;

}