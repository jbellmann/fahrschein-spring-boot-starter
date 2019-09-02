package org.zalando.spring.boot.nakadi.config;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.zalando.spring.boot.nakadi.NakadiListener;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class NakadiListenerContainer implements SmartLifecycle, InitializingBean {

    private ThreadPoolTaskScheduler scheduler;

    private AtomicReference<ScheduledFuture<?>> scheduledTaskReference = new AtomicReference<>();

    private Boolean autoStartup = true;

    private String consumerId = "CONSUMER_ID_NOT_SET";

    @NonNull
    private final NakadiConsumer nakadiConsumer;

    @NonNull
    private final NakadiListener<?> nakadiListener;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.consumerId = nakadiConsumer.getConsumerConfig().getId();
    }

    public synchronized void initialize() {
        if(scheduler == null) {
            scheduler = new ThreadPoolTaskScheduler();
            scheduler.setWaitForTasksToCompleteOnShutdown(false);
            scheduler.setPoolSize(nakadiConsumer.getConsumerConfig().getThreads().getListenerPoolSize());
            scheduler.setThreadFactory(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new NakadiListenerThread(r, "nlc" + nakadiConsumer.getConsumerConfig().getId());
                }
            });
            scheduler.setBeanName("scheduler-nlc-" + nakadiConsumer.getConsumerConfig().getId());
            scheduler.afterPropertiesSet();
        }
    }

    @Override
    public void start() {
        log.info("Starting NakadiListener '{}' ...", consumerId);
        if (isRunning()) {
            log.info("... NakadiListener '{}' is already running", consumerId);
            return;
        }

        initialize();
        try {
            scheduledTaskReference.set(scheduler.scheduleAtFixedRate(nakadiConsumer.runnable(this.nakadiListener).unchecked(), 70 * 1_000));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("unable to start nakadi-listener " + consumerId, e);
        }
        log.info("... started NakadiListener '{}'", consumerId);
    }

    @Override
    public void stop() {
        log.info("Stopping NakadiListener '{}' ...", consumerId);

        if (!isRunning()) {
            log.info("... NakadiListener '{}' not running.", consumerId);
            return;
        }

        final ScheduledFuture<?> sf = this.scheduledTaskReference.get();
        if (sf != null) {
            sf.cancel(true);
        }
        scheduler.shutdown();
        scheduler = null;
        log.info("... stopped NakadiListener '{}'", consumerId);

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
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
