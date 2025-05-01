package net.badlion.potionchat;

import net.badlion.potpvp.PotPvP;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void channelCommand(PlayerCommandPreprocessEvent event) {
        // Don't let them set the party channel as their active channel if they're not in party
        if (event.getMessage().equalsIgnoreCase("/ch p")) {
            if (!PotPvP.getInstance().getPlayerGroup(event.getPlayer()).isParty()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You are not in a party!");
                event.setCancelled(true);
            }
        }
    }

}
