package us.zonix.core.shared.redis;

import lombok.Getter;
import redis.clients.jedis.JedisPool;

@Getter
public class JedisSettings {

	private final String address;
	private final int port;
	private final String password;
	private final JedisPool jedisPool;

	public JedisSettings(String address, int port, String password) {
		this.address = address;
		this.port = port;
		this.password = password;

		this.jedisPool = new JedisPool(this.address, this.port);
	}

	public JedisSettings(String address, String password) {
		this(address, 6379, password);
	}

	public JedisSettings(String address) {
		this(address, null);
	}

	public boolean hasPassword() {
		return this.password != null;
	}

}
