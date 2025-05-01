package net.badlion.gfactions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import net.badlion.gfactions.GFactions;

public class KickListener implements Listener {
	
	private GFactions plugin;
	
	public KickListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.LOWEST) 
	public void onKickedPlayer(PlayerKickEvent event) { 
		if (this.plugin.getPlayersToBeKicked().contains(event.getPlayer().getUniqueId().toString())) {
			this.plugin.getPlayersToBeKicked().remove(event.getPlayer().getUniqueId().toString());
			event.setLeaveMessage(null);
		}
	}

}
