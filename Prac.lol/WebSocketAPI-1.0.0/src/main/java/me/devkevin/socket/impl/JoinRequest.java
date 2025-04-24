package me.devkevin.socket.impl;

import lombok.RequiredArgsConstructor;
import me.devkevin.api.Request;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 20:14
 */
@RequiredArgsConstructor
public final class JoinRequest implements Request {
    private final InetAddress address;
    private final UUID uniqueId;

    private final String name;

    @Override public String getPath() {
        return "/player/" + this.uniqueId.toString() + "/joins/update/" + this.address + "/" + this.name;
    }

    @Override public Map<String, Object> toMap() {
        return null;
    }
}
