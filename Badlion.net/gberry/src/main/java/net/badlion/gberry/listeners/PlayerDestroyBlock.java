package net.badlion.gberry.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerDestroyBlock implements Listener {

	@EventHandler
	public void onPlayerPlaceBoat(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().getType() == Material.BOAT) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.WATER && event.getClickedBlock().getType() != Material.STATIONARY_WATER) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractAirBlock(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null
				&& event.getClickedBlock().getTypeId() == 36)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDestroyAirBlock(BlockBreakEvent event) {
		if (event.getBlock().getTypeId() == 36)
			event.setCancelled(true);
	}
	
}
