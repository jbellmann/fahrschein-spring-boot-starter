package org.zalando.spring.boot.nakadi.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.zalando.fahrschein.StreamParameters;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.Client.NakadiConsumerConfig;
import org.zalando.spring.boot.nakadi.config.NakadiClientsProperties.StreamParametersConfig;

public class StreamParametersConfigTest {

    @Test
    public void testStreamParametersNoConfig() {
        NakadiConsumerConfig config = new NakadiConsumerConfig();
        DefaultNakadiConsumer consumer = new DefaultNakadiConsumer(null, config);
        StreamParameters streamParamters = consumer.getStreamParameters();
        assertThat(streamParamters.getBatchFlushTimeout()).isEmpty();
        assertThat(streamParamters.getBatchLimit()).isEmpty();
        assertThat(streamParamters.getMaxUncommittedEvents()).isEmpty();
        assertThat(streamParamters.getStreamKeepAliveLimit()).isEmpty();
        assertThat(streamParamters.getStreamLimit()).isEmpty();
        assertThat(streamParamters.getStreamTimeout()).isEmpty();
        assertThat(streamParamters).isNotNull();
    }

    @Test
    public void testStreamParametersConfig() {
        NakadiConsumerConfig config = new NakadiConsumerConfig();
        StreamParametersConfig spc = new StreamParametersConfig();
        spc.setBatchLimit(40);
        spc.setStreamLimit(40);
        config.setStreamParameters(spc);
        DefaultNakadiConsumer consumer = new DefaultNakadiConsumer(null, config);
        StreamParameters streamParamters = consumer.getStreamParameters();
        assertThat(streamParamters.getBatchFlushTimeout()).isEmpty();
        assertThat(streamParamters.getBatchLimit()).isEqualTo(Optional.ofNullable(40));
        assertThat(streamParamters.getMaxUncommittedEvents()).isEmpty();
        assertThat(streamParamters.getStreamKeepAliveLimit()).isEmpty();
        assertThat(streamParamters.getStreamLimit()).isEqualTo(Optional.ofNullable(40));
        assertThat(streamParamters.getStreamTimeout()).isEmpty();
        assertThat(streamParamters).isNotNull();
    }
}
