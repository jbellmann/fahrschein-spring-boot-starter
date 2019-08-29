package org.zalando.spring.boot.nakadi.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.util.List;

import org.zalando.fahrschein.EventAlreadyProcessedException;
import org.zalando.spring.boot.nakadi.NakadiListener;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeterNakadiListener<T> implements NakadiListener<T> {

    private final NakadiListener<T> delegate;
    private final MeterRegistry meterRegistry;
    private Timer successTimer;
    private Timer failureTimer;
    private Counter inputCounter;

    public void init() {
        successTimer = meterRegistry.timer("success", Tags.empty());
        failureTimer = meterRegistry.timer("failure", Tags.empty());
        inputCounter = meterRegistry.counter("input", Tags.empty());
    }

    @Override
    public void accept(List<T> events) throws IOException, EventAlreadyProcessedException {
        inputCounter.increment(events != null ? events.size() : 0);
        long start = System.currentTimeMillis();
        boolean failure = false;
        try {
            this.delegate.accept(events);
        } catch (IOException e) {
            failure = true;
            throw e;
        } catch (EventAlreadyProcessedException e) {
            failure = true;
            throw e;
        } finally {
            if (failure) {
                failureTimer.record(System.currentTimeMillis() - start, MILLISECONDS);
            }else {
                successTimer.record(System.currentTimeMillis() - start, MILLISECONDS);
            }
        }
    }

    @Override
    public Class<T> getEventType() {
        return this.delegate.getEventType();
    }

}
