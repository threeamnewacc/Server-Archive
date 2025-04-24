package me.devkevin.landcore.utils.json;

import com.google.gson.JsonObject;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:38
 * JsonSerializer / me.devkevin.landcore.utils / LandCore
 */
public interface JsonSerializer<T> {
    JsonObject serialize(T t);
}
