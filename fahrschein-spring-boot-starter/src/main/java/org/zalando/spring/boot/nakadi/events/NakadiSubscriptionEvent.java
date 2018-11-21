package org.zalando.spring.boot.nakadi.events;

import org.zalando.fahrschein.StreamParameters;
import org.zalando.fahrschein.domain.Subscription;

import com.google.common.base.MoreObjects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NakadiSubscriptionEvent {

	private final String listenerId;
	private final Subscription subscription;
	private final StreamParameters streamParameters;
	private final String eventTypeClassName;
	private final String listenerClassName;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
							.add("listenerId", listenerId)
							.add("eventTypeClassname", eventTypeClassName)
							.add("listenerClassname", listenerClassName)
							.add("subscriptionId", subscription.getId())
							.add("subscriptionOwningApplication", subscription.getOwningApplication())
							.add("subscriptionConsumerGroup", subscription.getConsumerGroup())
							.add("subscriptionCreatedAt", subscription.getCreatedAt())
							.add("subscriptionEventTypes", subscription.getEventTypes())
							.add("streamParamsBatchFlushTimeout", streamParameters.getBatchFlushTimeout().orElse(-1))
							.add("streamParamsBatchLimit", streamParameters.getBatchLimit().orElse(-1))
							.add("streamParamsCommitTimeout", streamParameters.getCommitTimeout().orElse(-1))
							.add("streamParamsMaxUncommittedEvents", streamParameters.getMaxUncommittedEvents().orElse(-1))
							.add("streamParamsStreamKeepAliveLimit", streamParameters.getStreamKeepAliveLimit().orElse(-1))
							.add("streamParamsStreamLimit", streamParameters.getStreamLimit().orElse(-1))
							.add("streamParamsStreamTimeout", streamParameters.getStreamTimeout().orElse(-1))
							.toString();
	}
	
	
}
