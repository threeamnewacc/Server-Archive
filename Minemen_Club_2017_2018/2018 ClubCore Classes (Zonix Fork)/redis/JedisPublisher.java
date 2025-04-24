package us.zonix.core.shared.redis;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class JedisPublisher<K> {

	private final JedisSettings jedisSettings;
	private final String channel;

	public void write(K message) {
		Jedis jedis = null;

		try {
			jedis = this.jedisSettings.getJedisPool().getResource();

			if (this.jedisSettings.hasPassword()) {
				jedis.auth(this.jedisSettings.getPassword());
			}

			jedis.publish(this.channel, message.toString());
		}
		finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void writeDirectly(String message) {
		Jedis jedis = null;

		try {
			jedis = this.jedisSettings.getJedisPool().getResource();

			if (this.jedisSettings.hasPassword()) {
				jedis.auth(this.jedisSettings.getPassword());
			}

			jedis.publish(this.channel, message);
		}
		finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

}
