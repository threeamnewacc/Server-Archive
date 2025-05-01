package net.badlion.mpg.listeners;

import net.badlion.disguise.events.PlayerDisguiseEvent;
import net.badlion.disguise.events.PlayerUndisguiseEvent;
import net.badlion.mpg.MPG;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisguisedListener implements Listener {

    @EventHandler
    public void onPlayerDisguiseEvent(PlayerDisguiseEvent event) {
        if (!MPG.ALLOW_DISGUISING) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "/disguise is disabled for this game mode.");
        } else if (MPG.getInstance().getServerState() != MPG.ServerState.LOBBY) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Cannot use /disguise once the game has started");
        }
    }

    @EventHandler
    public void onPlayerUndisguiseEvent(PlayerUndisguiseEvent event) {
        if (!MPG.ALLOW_DISGUISING) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "/undisguise is disabled for this game mode.");
        } else if (MPG.getInstance().getServerState() != MPG.ServerState.LOBBY) {
	        event.setCancelled(true);
	        event.getPlayer().sendMessage(ChatColor.RED + "Cannot use /undisguise once the game has started");
        }
    }

}
