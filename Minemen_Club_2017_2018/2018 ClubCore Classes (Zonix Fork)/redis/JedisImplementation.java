package us.zonix.core.shared.redis;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class JedisImplementation {

	protected final JedisSettings jedisSettings;

	protected void cleanup(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	protected Jedis getJedis() {
		Jedis jedis = this.jedisSettings.getJedisPool().getResource();

		if (this.jedisSettings.hasPassword()) {
			jedis.auth(this.jedisSettings.getPassword());
		}

		return jedis;
	}

}
