package org.zalando.spring.boot.nakadi.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;
import org.zalando.fahrschein.IORunnable;
import org.zalando.spring.boot.nakadi.NakadiListener;
import org.zalando.spring.boot.nakadi.config.NakadiListenerContainer;

public class CustomContainerTest {

    @Test
    public void testCustomContainer() throws Exception {
        long sleep = 10;
        NakadiConsumer consumer = Mockito.mock(NakadiConsumer.class);
        ConsumerConfig cc = new ConsumerConfig();
        cc.setId("test_container");
        Mockito.when(consumer.getConsumerConfig()).thenReturn(cc);
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
        container.afterPropertiesSet();
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
