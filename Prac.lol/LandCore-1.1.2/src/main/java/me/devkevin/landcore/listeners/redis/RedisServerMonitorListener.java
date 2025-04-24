package me.devkevin.landcore.listeners.redis;

import lombok.RequiredArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 14/02/2023 @ 17:21
 * ServerMonitor / me.devkevin.landcore.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class RedisServerMonitorListener {
    private final LandCore plugin;

    @RedisHandler("server-monitor-add")
    public void onServerMonitorAdding(Map<String, Object> message) {
        String server = (String) message.get("server");

        plugin.getStaffManager().messageStaff(CC.D_GRAY + "[" + CC.YELLOW + "Server Monitor" + CC.D_GRAY + "] " + CC.WHITE + "Adding server " + server + "...");
    }

    @RedisHandler("server-monitor-remove")
    public void onServerMonitorRemoving(Map<String, Object> message) {
        String server = (String) message.get("server");

        plugin.getStaffManager().messageStaff(CC.D_GRAY + "[" + CC.YELLOW + "Server Monitor" + CC.D_GRAY + "] " + CC.WHITE + "Removing server " + server + "...");
    }
}
