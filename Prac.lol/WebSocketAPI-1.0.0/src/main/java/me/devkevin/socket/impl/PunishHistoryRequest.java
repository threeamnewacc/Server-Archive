package me.devkevin.socket.impl;

import lombok.RequiredArgsConstructor;
import me.devkevin.api.Request;

import java.util.Map;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 5:19
 */
@RequiredArgsConstructor
public final class PunishHistoryRequest implements Request {
    private final String name;

    @Override
    public String getPath() {
        return "/punishments/fetch/" + this.name;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
