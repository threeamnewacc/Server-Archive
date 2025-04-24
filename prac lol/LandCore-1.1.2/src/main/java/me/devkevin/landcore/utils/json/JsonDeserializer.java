package me.devkevin.landcore.utils.json;

import com.google.gson.JsonObject;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:40
 * JsonDeserializer / me.devkevin.landcore.utils.json / LandCore
 */
public interface JsonDeserializer<T> {
    T deserialize(JsonObject object);
}
