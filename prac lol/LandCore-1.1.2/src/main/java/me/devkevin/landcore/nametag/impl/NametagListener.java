package me.devkevin.landcore.nametag.impl;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 11:38
 * NametagListener / land.pvp.core.nametag.impl / LandCore
 */
@AllArgsConstructor
public class NametagListener implements Listener {

    private JavaPlugin plugin;

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setMetadata("NameTag-LoggedIn", new FixedMetadataValue(plugin, true));

        InternalNametag.initiatePlayer(player);
        InternalNametag.reloadPlayer(player);
        InternalNametag.reloadOthersFor(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // fix async updater when player disconnect
        player.removeMetadata("NameTag-LoggedIn", plugin);

        InternalNametag.getTeamMap().remove(player.getName());
    }
}

