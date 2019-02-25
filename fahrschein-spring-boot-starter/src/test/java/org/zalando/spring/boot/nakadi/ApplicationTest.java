package org.zalando.spring.boot.nakadi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void contextLoads() {
    	Map<String,CloseableNakadiClient> clientBeans = aac.getBeansOfType(CloseableNakadiClient.class);
    	assertThat(clientBeans).isNotNull();
    }
}
