package net.badlion.gguard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class RegionListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		//GGuard.getInstance().getLocationTask().getPlayerNameToRegionName().remove(event.getPlayer().getName());
	}

}
