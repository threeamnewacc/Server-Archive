package me.devkevin.landcore.listeners.redis;

import lombok.RequiredArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.redis.annotation.RedisHandler;
import me.devkevin.landcore.utils.message.CC;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 14:27
 * FactionListener / me.devkevin.landcore.listeners.redis / LandCore
 */
@RequiredArgsConstructor
public class RedisFactionListener {
    private final LandCore plugin;

    @RedisHandler("faction-join")
    public void onFactionJoin(Map<String, Object> message) {
        plugin.getFactionManager().broadcastToFactions(message.get("sender") + CC.GREEN + " has connected the ", (String) message.get("server"));
    }

    @RedisHandler("faction-left")
    public void onFactionLeft(Map<String, Object> message) {
        plugin.getFactionManager().broadcastToFactions(message.get("sender") + CC.RED + " has left the server.", (String) message.get("server"));
    }

    @RedisHandler("faction-chat")
    public void onFactionChat(Map<String, Object> message) {
        String sender = (String) message.get("sender");

        if (sender.equalsIgnoreCase("CONSOLE")) {
            plugin.getFactionManager().broadcastToFactionsChat(CC.RED + sender, (String) message.get("message"), "GLOBAL");
        }

        plugin.getFactionManager().broadcastToFactionsChat((String) message.get("format"), (String) message.get("message"), (String) message.get("server"));
    }
}
