package org.zalando.spring.boot.nakadi.config;

import java.io.Closeable;
import java.io.IOException;

import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.nakadi.CloseableNakadiClient;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class DefaultCloseableNakadiClient implements CloseableNakadiClient {

	@NonNull
	private final Closeable closeable;
	
	@Getter
	@NonNull
	private final NakadiClient delegate;
	
	@Override
	public void close() throws IOException {
		log.info("Close httpClient ...");
		closeable.close();
		log.info("HttpClient closed.");
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

}
