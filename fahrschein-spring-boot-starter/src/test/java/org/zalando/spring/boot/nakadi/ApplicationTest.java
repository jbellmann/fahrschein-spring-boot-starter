package org.zalando.spring.boot.nakadi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    public void contextLoads() {
    }
}
