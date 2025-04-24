package me.devkevin.landcore.listeners.redis;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 12:46
 * FrozenDisconnectListener / land.pvp.core.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class FrozenDisconnectListener {
    private final LandCore plugin;

    @RedisHandler("frozen-disconnect")
    public void onFrozenDisconnect(Map<String, Object> message) {
        plugin.getStaffManager().messageStaffWithPrefix(message.get("sender") + CC.PRIMARY + " left the server while frozen.", (String) message.get("server"));
    }
}
