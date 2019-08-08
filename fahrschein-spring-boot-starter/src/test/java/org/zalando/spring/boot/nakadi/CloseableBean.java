package org.zalando.spring.boot.nakadi;

import java.io.Closeable;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CloseableBean implements Closeable {

	@Override
	public void close() throws IOException {
		log.info("CLOSING CLOSEABLE BEAN ....");
	}

}
