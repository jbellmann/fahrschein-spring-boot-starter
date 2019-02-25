package org.zalando.spring.boot.nakadi;

import java.io.Closeable;

import org.springframework.beans.factory.DisposableBean;
import org.zalando.fahrschein.NakadiClient;

public interface CloseableNakadiClient extends Closeable, DisposableBean {
	
	NakadiClient getDelegate();
}
