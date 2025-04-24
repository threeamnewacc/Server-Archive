package me.devkevin.api;

import java.util.Map;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:33
 */
public interface Request {
    String getPath();
    Map<String, Object> toMap();
}
