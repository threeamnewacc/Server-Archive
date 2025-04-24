package me.devkevin.api;

import com.google.gson.JsonElement;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:34
 */
public interface Callback {
    void callback(JsonElement object);
}
