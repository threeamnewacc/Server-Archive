package me.devkevin.landcore.listeners.redis;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 10:38
 * StaffChatListener / land.pvp.core.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class StaffChatListener {
    private final LandCore plugin;

    @RedisHandler("staff-chat")
    public void onStaffChat(Map<String, Object> message) {
        String sender = (String) message.get("sender");

        if (sender.equalsIgnoreCase("CONSOLE")) {
            plugin.getStaffManager().messageStaff(CC.RED + sender, (String) message.get("message"), "GLOBAL");
        }

        plugin.getStaffManager().messageStaff((String) message.get("format"), (String) message.get("message"), (String) message.get("server"));
    }
}
