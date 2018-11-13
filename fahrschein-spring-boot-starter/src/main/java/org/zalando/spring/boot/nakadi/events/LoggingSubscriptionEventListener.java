package org.zalando.spring.boot.nakadi.events;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoggingSubscriptionEventListener {
	
	private final boolean enabled;

	@EventListener
	@Async
	public void onNakadiSubscriptionEvent(NakadiSubscriptionEvent e) {
		if(enabled) {
			log.info(e.toString());
		}
	}
}
