package org.zalando.spring.boot.nakadi;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.zalando.fahrschein.EventAlreadyProcessedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExampleNakadiListener implements NakadiListener<ExampleEvent>{

    @Override
    public void accept(List<ExampleEvent> events) throws IOException, EventAlreadyProcessedException {
        log.info("GOT EXAMPLE_EVENT : {}", events);
    }

    @Override
    public Class<ExampleEvent> getEventType() {
        return ExampleEvent.class;
    }

}
