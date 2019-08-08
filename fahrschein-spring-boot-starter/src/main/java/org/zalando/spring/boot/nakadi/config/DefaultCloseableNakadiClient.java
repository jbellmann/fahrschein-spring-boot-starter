package org.zalando.spring.boot.nakadi.config;

import static java.lang.Integer.MIN_VALUE;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.nakadi.CloseableNakadiClient;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class DefaultCloseableNakadiClient implements CloseableNakadiClient {

	private AtomicBoolean running = new AtomicBoolean(false);

	@NonNull
	private final Closeable closeable;

	@Getter
	@NonNull
	private final NakadiClient delegate;

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	@Override
	public void start() {
		log.info("START ....");
		if(isRunning()) {
			log.info("ALREADY RUNNING, SKIP STARTING ...");
			return;
		}
		running.set(true);
		log.info("STARTED : {}", isRunning());
	}

	@Override
	public void stop() {
		log.info("STOP ....");
		if(!isRunning()) {
			log.info("NOT RUNNING, SKIP STOPPING");
			return;
		}
		try {
			log.info("Close httpClient ...");
			closeable.close();
			log.info("HttpClient closed.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			running.set(false);
		}
		log.info("STOPPED : {}", !isRunning());
	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

	@Override
	public int getPhase() {
		return MIN_VALUE;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

}
