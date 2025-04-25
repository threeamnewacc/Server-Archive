package us.zonix.api.jedis;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Getter
public class RedisController {

	private final String address;
	private final String password;
	private final int port;
	private final JedisPool pool;

	public RedisController(String address, String password, int port) {
		this.address = address;
		this.password = password;
		this.port = port;
		this.pool = new JedisPool(address, port);

		if (this.password != null) {
			Jedis jedis = null;

			try {
				jedis = this.pool.getResource();
				jedis.auth(this.password);
			} catch (Exception e) {
				e.printStackTrace();

				if (jedis != null) {
					jedis.close();
				}
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
	}

	public boolean hasPassword() {
		return password != null;
	}

}
