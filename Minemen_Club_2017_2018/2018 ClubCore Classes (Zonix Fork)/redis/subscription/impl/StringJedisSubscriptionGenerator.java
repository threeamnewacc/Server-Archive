package us.zonix.core.shared.redis.subscription.impl;

import us.zonix.core.shared.redis.subscription.JedisSubscriptionGenerator;

public class StringJedisSubscriptionGenerator implements JedisSubscriptionGenerator<String> {

	@Override
	public String generateSubscription(String message) {
		return message;
	}

}
