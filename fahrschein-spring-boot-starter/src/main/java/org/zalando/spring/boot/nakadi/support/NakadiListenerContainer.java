package org.zalando.spring.boot.nakadi.support;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.SmartLifecycle;
import org.zalando.fahrschein.IORunnable;
import org.zalando.fahrschein.Listener;
import org.zalando.spring.boot.nakadi.NakadiConsumer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * To manage lifecycle of {@link Listener} in Spring.
 */
@Slf4j
@RequiredArgsConstructor
public class NakadiListenerContainer<T> implements SmartLifecycle, BeanNameAware {

    private final AtomicBoolean running = new AtomicBoolean(false);

    @NonNull
    private final ExecutorService executorService;

    @NonNull
    private final NakadiConsumer nakadiConsumer;

    @NonNull
    private final Class<T> eventClass;

    @NonNull
    private final Listener<T> listener;

    protected int phase = Integer.MAX_VALUE;

    protected boolean autoStartup = true;

    private Future<Void> future;

    protected String beanName = "NO_NAME_ASSIGNED";

    @Override
    public void start() {
        if (isRunning()) {
            log.info("ListenerContainer is already running.");
            return;
        }
        try {
            this.future = submit();
            running.set(true);
            log.info("ListenerContainer {} running.", this.beanName);
        } catch (IOException e) {
            log.warn("ListenerContainer {} unable to start.", this.beanName, e);
            running.set(false);
        }
    }

    @SuppressWarnings("unchecked")
    protected Future<Void> submit() throws IOException {
        return (Future<Void>) executorService
                .submit(new loggingIORunnable(nakadiConsumer.runnable(this.eventClass, this.listener)).unchecked());
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            log.info("ListenerContainer not running");
            return;
        }
        boolean canceled = this.future.cancel(true);
        if (!canceled) {
            log.info("ListenerContainer-Future could not be canceled.");
        }
        this.running.set(false);
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public int getPhase() {
        return phase;
    }

    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    @Slf4j
    @RequiredArgsConstructor
    protected static class loggingIORunnable implements IORunnable {

        @NonNull
        private final IORunnable delegate;

        @Override
        public void run() throws IOException {
            log.info("NAKADI_LISTENER_CONTAINER : BEFORE RUN NAKADI_RUNNABLE");
            this.delegate.run();
            log.info("NAKADI_LISTENER_CONTAINER : AFTER RUN NAKADI_RUNNABLE");
        }

    }
}
