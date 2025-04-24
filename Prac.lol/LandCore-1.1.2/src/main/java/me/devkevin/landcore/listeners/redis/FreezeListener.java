package me.devkevin.landcore.listeners.redis;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 12:20
 * FreezeListener / land.pvp.core.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class FreezeListener {
    private final LandCore plugin;

    @RedisHandler("freeze-listener")
    public void onFreeze(Map<String, Object> map) {
        String sender = (String) map.get("sender");
        String target = (String) map.get("frozen");
        String server = (String) map.get("server");

        boolean frozen = Boolean.parseBoolean((String) map.get("isFrozen"));

        plugin.getStaffManager().messageStaffWithPrefix(CC.PRIMARY + sender + CC.SECONDARY + " has " + (frozen ? "frozen " : "unfrozen ") + target + ".", server);
    }
}
