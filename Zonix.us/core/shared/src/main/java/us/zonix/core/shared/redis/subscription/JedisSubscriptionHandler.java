package us.zonix.core.shared.redis.subscription;

/**
 * @since 2017-09-02
 */
public interface JedisSubscriptionHandler<K> {

	void handleMessage(K object);

}
