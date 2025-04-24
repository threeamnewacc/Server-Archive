package club.minemen.practice.jedis;

import club.minemen.core.redis.subscription.JedisSubscriptionHandler;
import com.google.gson.JsonObject;

/**
 * @since 11/12/2017
 */
public class JedisHandler implements JedisSubscriptionHandler<JsonObject> {

	@Override
	public void handleMessage(JsonObject jsonObject) {
		String action = jsonObject.get("action").getAsString();
		if (action.equalsIgnoreCase("reset")) {
			String type = jsonObject.get("type").getAsString();

			if (type.equalsIgnoreCase("premium_matches")) {

			} else if (type.equalsIgnoreCase("premium_elo")) {

			}
		}
	}
}
