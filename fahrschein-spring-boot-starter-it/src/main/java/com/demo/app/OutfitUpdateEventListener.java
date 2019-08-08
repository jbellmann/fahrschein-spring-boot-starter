package com.demo.app;

import java.io.IOException;
import java.util.List;

import org.zalando.fahrschein.EventAlreadyProcessedException;
import org.zalando.fahrschein.Listener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class OutfitUpdateEventListener implements Listener<OutfitUpdateEvent> {

    @Override
    public void accept(List<OutfitUpdateEvent> events) throws IOException, EventAlreadyProcessedException {
        log.info("HANDLE EVENTS, SIZE : {}", events.size());
        events.forEach(e -> 
            log.info("OUTFIT_UPDATE_EVENT FOR ID : {}", e.getData().getOutfitId())
        );
        log.info("ALL EVENTS HANDLED");
    }
    
}