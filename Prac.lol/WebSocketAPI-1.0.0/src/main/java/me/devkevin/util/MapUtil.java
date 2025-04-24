package me.devkevin.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 5:08
 */
public class MapUtil {
    private MapUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static Map<String, Object> of(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Uneven number of arguments.");
        }

        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < args.length; i += 2) {
            String key = args[0].toString();
            Object value = args[i + 1];

            map.put(key, value);
        }
        return map;
    }
}
