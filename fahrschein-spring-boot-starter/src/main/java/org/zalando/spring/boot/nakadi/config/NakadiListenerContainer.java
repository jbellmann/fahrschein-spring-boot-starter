package org.zalando.spring.boot.nakadi.config;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.NakadiListener;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class NakadiListenerContainer implements SmartLifecycle, BeanNameAware {

    private ThreadPoolTaskScheduler scheduler;

    private AtomicReference<ScheduledFuture<?>> scheduledTaskReference = new AtomicReference<>();

    private boolean autoStartup = true;

    private String beanName = "BEAN_NAME_NOT_SET";

    @NonNull
    private final NakadiConsumer nakadiConsumer;

    @NonNull
    private final NakadiListener<?> nakadiListener;

    public synchronized void initialize() {
        if(scheduler == null) {
            scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.setThreadFactory(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new NakadiListenerThread(r, beanName + "-nakadiListenerContainer");
                }
            });
            scheduler.setBeanName(beanName + "-ThreadPoolTaskScheduler");
            scheduler.afterPropertiesSet();
        }
    }

    @Override
    public void start() {
        log.info("Starting NakadiListener ...");
        if (isRunning()) {
            log.info("... NakadiListener is already running");
            return;
        }

        initialize();

        try {
            scheduledTaskReference.set(scheduler.scheduleAtFixedRate(nakadiConsumer.runnable(this.nakadiListener).unchecked(), Duration.ofSeconds(70)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("unable to start nakadi-listener", e);
        }
        log.info("... started NakadiListener");
    }

    @Override
    public void stop() {
        log.info("Stopping NakadiListener ...");

        if (!isRunning()) {
            log.info("... NakadiListener not running.");
            return;
        }

        final ScheduledFuture<?> sf = this.scheduledTaskReference.get();
        if (sf != null) {
            sf.cancel(true);
        }
        scheduler.shutdown();
        scheduler = null;
        log.info("... stopped NakadiListener");

    }

    @Override
    public boolean isRunning() {
        final ScheduledFuture<?> sf = this.scheduledTaskReference.get();
        if (sf == null) {
            return false;
        } else {
            return !(sf.isDone() || sf.isCancelled());
        }
    }

    @Override
    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    static class NakadiListenerThread extends Thread {
        NakadiListenerThread(Runnable target, String name) {
            super(target, name);
            this.setDaemon(true);
        }
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

}
