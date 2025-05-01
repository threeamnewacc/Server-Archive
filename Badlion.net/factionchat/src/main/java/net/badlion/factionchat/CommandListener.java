package net.badlion.factionchat;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void channelCommand(PlayerCommandPreprocessEvent event) {
        // Don't let them set the party channel as their active channel if they're not in party
        if (event.getMessage().equalsIgnoreCase("/ch f")) {
	        Faction faction = FPlayers.i.get(event.getPlayer()).getFaction();
            if (faction == null || faction.getId().equals("0")) {
                event.getPlayer().sendMessage(ChatColor.RED + "You are not in a faction!");

	            // They might've just disbanded/left a faction, set global as active channel
	            ChatSettingsManager.getChatSettings(event.getPlayer()).setActiveChannel("G");

                event.setCancelled(true);
            }
        }
    }

}
