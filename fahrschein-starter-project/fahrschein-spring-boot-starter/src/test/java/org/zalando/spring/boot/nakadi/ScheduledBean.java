package org.zalando.spring.boot.nakadi;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledBean {

	@Scheduled(fixedRate = 250)
	public void scheduledMethod() {
		log.info("RUNNING SCHEDULED METHOD :::");
	}
	
	@Scheduled(fixedRate = 250)
	public void scheduledSleepMethod() {
		try {
			log.info("RUNNING SLEEP_SCHEDULED METHOD :::");
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}
}
