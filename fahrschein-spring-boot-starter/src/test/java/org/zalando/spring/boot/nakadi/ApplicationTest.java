package org.zalando.spring.boot.nakadi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.fahrschein.NakadiClient;

// https://github.com/zalando-nakadi/fahrschein#stopping-and-resuming-streams
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    @Qualifier("example")
    private NakadiConsumer exampleNakadi;

    @Autowired
    @Qualifier("firstPublisher")
    private NakadiPublisher publisher;

    @Autowired
    private AbstractApplicationContext aac;

    @Test
    public void contextLoads() throws InterruptedException {
        Map<String, NakadiClient> clientBeans = aac.getBeansOfType(NakadiClient.class);
        TimeUnit.SECONDS.sleep(5);
        assertThat(clientBeans).isNotNull();
    }
}
