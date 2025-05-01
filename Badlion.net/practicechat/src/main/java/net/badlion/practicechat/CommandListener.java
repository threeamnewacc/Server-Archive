package net.badlion.practicechat;

import io.kohi.kpractice.PracticePlugin;
import io.kohi.kpractice.type.Party;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void channelCommand(PlayerCommandPreprocessEvent event) {
        // Don't let them set the party channel as their active channel if they're not in party
        if (event.getMessage().equalsIgnoreCase("/ch p")) {
            Party party = PracticePlugin.getInstance().getPartyManager().getParty(event.getPlayer());
            if (party == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You are not in a party!");
                event.setCancelled(true);
            }
        }
    }

}
