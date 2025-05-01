package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This is a temporary class until I can fix the actual GCHEAT plugin
 */
public class GCheatListener implements Listener {

	private GFactions plugin;

	public GCheatListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onFreeCamChestUsage(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			Material type = block.getType();
			if (type == Material.CHEST || type == Material.FURNACE || type == Material.BURNING_FURNACE || type == Material.BREWING_STAND
					|| type == Material.DISPENSER || type == Material.DROPPER || type == Material.TRAPPED_CHEST || type == Material.HOPPER) {
				Player player = event.getPlayer();
				Location playerLocation = player.getLocation();
				Location blockLocation = block.getLocation();

				if (blockLocation.distance(playerLocation) > 7.5) {
					this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "ac [WARNING]: " + player.getName() + " is possibly freecaming! " + player.getUniqueId());
					event.setCancelled(true);
				}
			}
		}
	}

}
