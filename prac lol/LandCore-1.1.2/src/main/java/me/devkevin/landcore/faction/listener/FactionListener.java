package me.devkevin.landcore.faction.listener;

import com.google.common.collect.Maps;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 1:29
 * FactionListener / me.devkevin.landcore.faction.listener / LandCore
 */
public class FactionListener implements Listener {
    private final LandCore plugin = LandCore.getInstance();

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        this.plugin.getFactionManager().addPlayer(player);

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        if (factionProfile == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Messages.DATA_LOAD_FAIL);
            return;
        }

        this.plugin.getFactionManager().loadPlayerFaction(player);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CoreProfile profile = this.plugin.getProfileManager().getProfile(player.getUniqueId());

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        if (factionProfile != null) {
            Map<String, Object> message = Maps.newHashMap();
            message.put("server", this.plugin.getServerName());
            message.put("sender", profile.getRank().getColor() + player.getName());

            plugin.getRedisMessenger().send("faction-join", message);
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.plugin.getFactionManager().removePlayer(player);
        this.plugin.getFactionManager().savePlayerFaction(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();

        CoreProfile coreProfile = this.plugin.getProfileManager().getProfile(player.getUniqueId());
        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        if (factionProfile != null && factionProfile.getFaction() != null && factionProfile.getFaction().isFactionChat()) {
            Map<String, Object> message = Maps.newHashMap();
            message.put("server", plugin.getServerName());
            message.put("format", coreProfile.getRank().getColor() + player.getName());
            message.put("message", chatMessage);
            message.put("sender", event.getPlayer().getName());

            plugin.getRedisMessenger().send("faction-chat", message);
        }
    }
}
