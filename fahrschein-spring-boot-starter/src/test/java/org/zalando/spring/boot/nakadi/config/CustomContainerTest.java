package org.zalando.spring.boot.nakadi.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;
import org.zalando.fahrschein.IORunnable;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.NakadiListener;
import org.zalando.spring.boot.nakadi.config.NakadiListenerContainer;

public class CustomContainerTest {

    @Test
    public void testCustomContainer() throws InterruptedException, IOException {
        long sleep = 20;
        NakadiConsumer consumer = Mockito.mock(NakadiConsumer.class);
        NakadiListener<?> listener = Mockito.mock(NakadiListener.class);
        Mockito.when(consumer.runnable(listener)).thenReturn(new IORunnable() {
            
            @Override
            public void run() throws IOException {
                try {
                    TimeUnit.SECONDS.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        });
        NakadiListenerContainer container = new NakadiListenerContainer(consumer, listener);
        container.setBeanName("test_container");
        container.initialize();
        assertThat(container.isRunning()).isFalse();
        assertThat(container.isAutoStartup()).isTrue();
        container.start();
        assertThat(container.isRunning()).isTrue();
        TimeUnit.SECONDS.sleep(sleep + 5);
        assertThat(container.isRunning()).isTrue();
        container.stop();
        assertThat(container.isRunning()).isFalse();
    }

    
}
