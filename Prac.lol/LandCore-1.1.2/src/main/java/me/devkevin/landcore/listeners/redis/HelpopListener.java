package me.devkevin.landcore.listeners.redis;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 12:19
 * HelpopListener / land.pvp.core.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class HelpopListener {
    private final LandCore plugin;

    @RedisHandler("help-op")
    public void onHelpOp(Map<String, Object> map) {
        String server = (String) map.get("server");
        String player = (String) map.get("player");
        String request = (String) map.get("request");
        plugin.getStaffManager().messageStaff(CC.RED + "\n(HelpOp) " + CC.D_AQUA + "[" + server + "] " + CC.SECONDARY + player
                + CC.PRIMARY + " requested assistance: " + CC.SECONDARY + request + CC.PRIMARY + ".\n ");
    }
}
