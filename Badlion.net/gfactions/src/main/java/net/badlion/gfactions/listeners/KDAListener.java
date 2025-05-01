package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class KDAListener implements Listener {

	private GFactions plugin;

	public KDAListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent event) {
		// Get KDAPlayer object
		this.plugin.getKdaManager().getKDAPlayer(event.getPlayer());
	}

	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent event) {
		// Remove KDAPlayer object
		this.plugin.getKdaManager().removeKDAPlayer(event.getPlayer());
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent event) {
		// Add a death to the player
		this.plugin.getKdaManager().getKDAPlayer(event.getEntity()).addDeath();

		// Add a kill to the killer if killer exists
		if (event.getEntity().getKiller() != null) {
			this.plugin.getKdaManager().getKDAPlayer(event.getEntity().getKiller()).addKill();
		}
	}

}
