package net.badlion.build.listeners;

import net.badlion.build.BuildPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GameModeListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.gamemodeCreative(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        event.getPlayer().setGameMode(GameMode.CREATIVE);
    }

    private void gamemodeCreative(final Player player) {
        new BukkitRunnable() {
            public void run() {
                player.setGameMode(GameMode.CREATIVE);
            }
        }.runTaskLater(BuildPlugin.getInstance(), 1);
    }

}
