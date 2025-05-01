package net.badlion.gberry.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.tinywebteam.badlion.MineKart;

public class BlockBreakListener implements Listener {
	
	private MineKart plugin;

	public BlockBreakListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			if (!player.isOp()) {
				event.setCancelled(true);
			}
		} else {
			if (this.plugin.getBlocksToBeRemoved().contains(event.getBlock())) {
				event.getBlock().getDrops().clear();
				this.plugin.getBlocksToBeRemoved().remove(event.getBlock());
			}
		}
	}
}
