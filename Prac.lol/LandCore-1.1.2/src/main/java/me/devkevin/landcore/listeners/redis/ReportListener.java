package me.devkevin.landcore.listeners.redis;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 10:38
 * ReportListener / land.pvp.core.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class ReportListener {
    private final LandCore plugin;

    @RedisHandler("report")
    public void onReport(Map<String, Object> map) {
        String player = (String) map.get("reporter");
        String target = (String) map.get("reported");
        String report = (String) map.get("reason");
        String server = (String) map.get("server");

        plugin.getStaffManager().messageStaff("");
        plugin.getStaffManager().messageStaff(CC.RED + "(Report) " + CC.D_AQUA + "[" + server + "] " + CC.SECONDARY + player + CC.PRIMARY
                + " reported " + CC.SECONDARY + target + CC.PRIMARY + " for " + CC.SECONDARY + report + CC.PRIMARY + ".");
        plugin.getStaffManager().messageStaff("");
    }
}
