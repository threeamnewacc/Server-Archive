package me.devkevin.socket.impl;

import lombok.RequiredArgsConstructor;
import me.devkevin.api.Request;
import me.devkevin.util.MapUtil;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 5:21
 */
@RequiredArgsConstructor
public final class PunishmentRequest implements Request {
    private final Timestamp expiry;
    private final String ipAddress, reason, name, type;
    private final int playerUUID, uuid;

    @Override
    public String getPath() {
        return "/punishments/punish";
    }

    @Override
    public Map<String, Object> toMap() {
        return MapUtil.of(
                this.name != null ? "name" : "player-uuid", this.name != null ? this.name : this.playerUUID,
                "ip-address", this.ipAddress == null ? "UNKNOWN" : this.ipAddress,
                "expiry", this.expiry == null ? "PERM" : this.expiry,
                "reason", this.reason,
                "punisher", this.uuid,
                "type", this.type
        );
    }
}
