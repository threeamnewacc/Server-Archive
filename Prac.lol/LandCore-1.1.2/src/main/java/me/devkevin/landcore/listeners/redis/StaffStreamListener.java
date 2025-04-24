package me.devkevin.landcore.listeners.redis;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 10:30
 * StaffStreamListener / land.pvp.core.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class StaffStreamListener {
    private final LandCore plugin;

    @RedisHandler("staff-join")
    public void onStaffJoin(Map<String, Object> message) {
        plugin.getStaffManager().messageStaffWithPrefix(message.get("sender") + CC.PRIMARY + " joined the server.", (String) message.get("server"));
    }

    @RedisHandler("staff-left")
    public void onStaffLeft(Map<String, Object> message) {
        plugin.getStaffManager().messageStaffWithPrefix(message.get("sender") + CC.PRIMARY + " left the server.", (String) message.get("server"));
    }
}
