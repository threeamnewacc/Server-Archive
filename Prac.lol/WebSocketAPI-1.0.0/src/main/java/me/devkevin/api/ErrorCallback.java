package me.devkevin.api;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:35
 */
public interface ErrorCallback extends Callback {
    void onError(String message);
}
