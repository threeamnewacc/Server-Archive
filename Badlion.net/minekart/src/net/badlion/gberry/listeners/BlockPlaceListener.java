package net.badlion.gberry.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;

public class BlockPlaceListener implements Listener {
	
	private MineKart plugin;
	
	public BlockPlaceListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Racer racer = this.plugin.getPlayerToRacer().get(player);
		if (racer != null) {
			// tnt
			if (event.getBlock().getTypeId() == 46) {
				event.getBlock().breakNaturally();
			}
			racer.getRace().getRemoveBlocksFromTrack().add(event.getBlock());
		} else if (!player.isOp()) {
			event.setCancelled(true);
		}
	}
}
