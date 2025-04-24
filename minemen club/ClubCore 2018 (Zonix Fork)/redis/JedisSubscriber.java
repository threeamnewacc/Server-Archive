package us.zonix.core.shared.redis;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import us.zonix.core.shared.redis.subscription.JedisSubscriptionGenerator;
import us.zonix.core.shared.redis.subscription.JedisSubscriptionHandler;
import us.zonix.core.shared.redis.subscription.impl.JsonJedisSubscriptionGenerator;
import us.zonix.core.shared.redis.subscription.impl.StringJedisSubscriptionGenerator;

public class JedisSubscriber<K> {

	private static final Map<Class, JedisSubscriptionGenerator> GENERATORS = new HashMap<>();

	static {
		GENERATORS.put(String.class, new StringJedisSubscriptionGenerator());
		GENERATORS.put(JsonObject.class, new JsonJedisSubscriptionGenerator());
	}

	protected final String channel;
	private final Class<K> typeParameter;
	@Getter private final JedisSettings jedisSettings;
	@Getter private final Jedis jedis;
	@Getter private JedisPubSub pubSub;

	private JedisSubscriptionHandler<K> jedisSubscriptionHandler;

	public JedisSubscriber(JedisSettings jedisSettings, String channel, Class<K> typeParameter, JedisSubscriptionHandler<K> jedisSubscriptionHandler) {
		this.jedisSettings = jedisSettings;
		this.channel = channel;
		this.typeParameter = typeParameter;
		this.jedisSubscriptionHandler = jedisSubscriptionHandler;

		this.pubSub = new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				JedisSubscriptionGenerator<K> jedisSubscriptionGenerator = GENERATORS.get(typeParameter);

				if (jedisSubscriptionGenerator != null) {
					K object = jedisSubscriptionGenerator.generateSubscription(message);
					JedisSubscriber.this.jedisSubscriptionHandler.handleMessage(object);
				}
				else {
					System.out.println("Generator type is null");
				}
			}
		};

		this.jedis = new Jedis(this.jedisSettings.getAddress(), this.jedisSettings.getPort());
		this.authenticate();
		this.connect();
	}

	private void authenticate() {
		if (this.jedisSettings.hasPassword()) {
			this.jedis.auth(this.jedisSettings.getPassword());
		}
	}

	private void connect() {
		Logger.getGlobal().info("Jedis is now reading on " + this.channel);

		new Thread(() -> {
			try {
				this.jedis.subscribe(this.pubSub, this.channel);
			}
			catch (Exception e) {
				e.printStackTrace();
				Logger.getGlobal().info("For some odd reason our JedisSubscriber(" + this.channel + ") threw an exception");
				close();
				connect();
			}
		}).start();
	}

	public void close() {
		Logger.getGlobal().info("Jedis is no longer reading on " + this.channel);

		if (this.pubSub != null) {
			this.pubSub.unsubscribe();
		}

		if (this.jedis != null) {
			this.jedis.close();
		}
	}

}
