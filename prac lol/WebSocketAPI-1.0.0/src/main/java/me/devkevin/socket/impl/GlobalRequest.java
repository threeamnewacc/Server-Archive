package me.devkevin.socket.impl;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import me.devkevin.api.Request;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 19:40
 */
@RequiredArgsConstructor
public final class GlobalRequest implements Request {
    private final InetAddress address;
    private final UUID uniqueId;
    private final String name;

    @Override public String getPath() {
        return "/player/" + this.uniqueId.toString() + "/global";
    }

    @Override public Map<String, Object> toMap() {
        return ImmutableMap.of(
                "name", this.name,
                "ip", this.address.getHostAddress()
        );
    }
}
