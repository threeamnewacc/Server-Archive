package me.devkevin.landcore.redis.message;

import com.google.gson.reflect.TypeToken;
import me.devkevin.landcore.LandCore;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 10:24
 * RedisMessage / land.pvp.core.redis.message / LandCore
 */
public class RedisMessage {

    public static Map<String, Object> deserialize(String string) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return LandCore.GSON.fromJson(string, type);
    }

    public static String serialize(Map<String, Object> map) {
        return LandCore.GSON.toJson(map);
    }

}
