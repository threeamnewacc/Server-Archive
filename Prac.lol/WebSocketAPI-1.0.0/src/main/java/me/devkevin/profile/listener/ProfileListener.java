package me.devkevin.profile.listener;

import club.udrop.core.Core;
import club.udrop.core.CoreAPI;
import club.udrop.core.api.player.PlayerData;
import me.devkevin.WebSocketAPI;
import me.devkevin.profile.Profile;
import me.devkevin.socket.impl.JoinRequest;
import me.devkevin.util.StringUtil;
import me.devkevin.util.wrapper.BanWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 19:46
 */
public class ProfileListener implements Listener {
    private final WebSocketAPI plugin = WebSocketAPI.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginLow(AsyncPlayerPreLoginEvent event) {
        this.plugin.getProfileManager().addPlayer(event.getUniqueId(), event.getName(), event.getAddress());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginHigh(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        Profile profile = this.plugin.getProfileManager().getPlayer(event.getUniqueId());
        PlayerData playerData = Core.INSTANCE.getPlayerManagement().getPlayerData(profile.getUuid());

        BanWrapper wrapper = profile.fetchData();
        if (playerData.getPunishData().isBlacklisted() || (wrapper.isBanned() && !CoreAPI.INSTANCE.getServerData("Hub").getServerName().toLowerCase().contains("Hub"))) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, wrapper.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getPlayer(player.getUniqueId());
        PlayerData playerData = CoreAPI.INSTANCE.getPlayerData(player.getUniqueId());

        // API might be processing them slowly, so wait for the retrieve event
        if (profile == null) {
            player.kickPlayer(StringUtil.LOAD_ERROR);
            return;
        }

        this.plugin.getProcessor()
                .sendRequestAsync(new JoinRequest(profile.getIpAddress(), profile.getUuid(), playerData.getPlayerName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.plugin.getProfileManager().removePlayer(player.getUniqueId());
    }
}
