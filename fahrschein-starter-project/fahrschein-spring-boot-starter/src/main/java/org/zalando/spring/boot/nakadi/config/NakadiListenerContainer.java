package org.zalando.spring.boot.nakadi.config;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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

    private Boolean autoStartup = true;

    private String beanName = "BEAN_NAME_NOT_SET";

    @NonNull
    private final NakadiConsumer nakadiConsumer;

    @NonNull
    private final NakadiListener<?> nakadiListener;

    public synchronized void initialize() {
        if(scheduler == null) {
            scheduler = new ThreadPoolTaskScheduler();
            scheduler.setWaitForTasksToCompleteOnShutdown(false);
            scheduler.setPoolSize(5);
            scheduler.setThreadFactory(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new NakadiListenerThread(r, beanName);
                }
            });
            scheduler.setBeanName("taskScheduler-" + beanName);
            scheduler.afterPropertiesSet();
        }
    }

    @Override
    public void start() {
        log.info("Starting NakadiListener {} ...", beanName);
        if (isRunning()) {
            log.info("... NakadiListener {} is already running", beanName);
            return;
        }

        initialize();
        try {
            scheduledTaskReference.set(scheduler.scheduleAtFixedRate(nakadiConsumer.runnable(this.nakadiListener).unchecked(), 70 * 1_000));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("unable to start nakadi-listener " + beanName, e);
        }
        log.info("... started NakadiListener {}", beanName);
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
        log.info("... stopped NakadiListener {}", beanName);

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

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
