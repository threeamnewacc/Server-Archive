package me.devkevin.landcore.redis;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.redis.message.RedisMessage;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 10:24
 * RedisMessenger / land.pvp.core.redis / LandCore
 */
@Getter
public class RedisMessenger {

    private final JedisPool jedisPool;
    private final LandCore plugin;
    private Set<Object> listeners = new HashSet<>();

    private boolean active = false;

    public RedisMessenger(LandCore plugin, String host, int port, int timeout, String password) {
        this.plugin = plugin;
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(20);
        this.jedisPool = new JedisPool(config, host, port, timeout, password);
    }

    public void initialize() {

        //In order to avoid subscribing twice to the same redis channel, we add a simple set.
        Set<String> subscribedChannels = Sets.newHashSet();
        Map<String, Pair<Object, Method>> map = Maps.newHashMap();

        //We schedule an asynchronous task to handle our subscriptions.
        listeners.forEach(listener -> {
            //After looping through each listener, we get that listener's methods, and try to find where the RedisHandler annotation is used, we add that to a set.
            Set<Method> methods = getMethodsOfAnnotation(listener.getClass(), RedisHandler.class);

            for (Method method : methods) {
                //For each of these sets, we get the redis handler, check if we're already subscribed, if not, we subscribe to the channel.
                RedisHandler handler = method.getAnnotation(RedisHandler.class);
                if (!subscribedChannels.contains(handler.value())) {
                    map.put(handler.value(), new ImmutablePair<>(listener, method));
                    subscribedChannels.add(handler.value());
                    active = true;
                }
            }
        });

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        map.forEach((c, pair) -> {
                            if (channel.equalsIgnoreCase(c)) {
                                try {
                                    pair.getValue().invoke(pair.getKey(), RedisMessage.deserialize(message));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }, subscribedChannels.toArray(new String[0]));
            }
        });
    }

    public void send(String channel, Map<String, Object> message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(channel, RedisMessage.serialize(message));
            }
        });
    }

    public void sendOff(String channel, Map<String, Object> message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, RedisMessage.serialize(message));
        }
    }

    public boolean isRedisConnected() {
        return this.jedisPool != null && !this.jedisPool.isClosed();
    }

    public void registerListeners(Object... objects) {
        for (Object object : objects) {
            getListeners().add(object);
        }
    }

    private Set<Method> getMethodsOfAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }
}
