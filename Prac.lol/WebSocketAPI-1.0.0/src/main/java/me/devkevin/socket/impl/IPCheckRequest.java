package me.devkevin.socket.impl;

import lombok.RequiredArgsConstructor;
import me.devkevin.api.Request;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:58
 */
@RequiredArgsConstructor
public final class IPCheckRequest implements Request {
    private final InetAddress address;
    private final UUID uuid;

    @Override
    public String getPath() {
        return "/player/" + this.uuid.toString() + "/ip-check/" + this.address.getHostAddress();
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
