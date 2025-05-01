package net.badlion.gberry.listeners;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;
import com.tinywebteam.badlion.tasks.BlueShellDelayTask;
import com.tinywebteam.badlion.tasks.RedShellDelayTask;

public class PlayerInteractListener implements Listener {
	
	private MineKart plugin;
	
	public PlayerInteractListener(MineKart plugin) {
		this.plugin = plugin;	
	}
	
	@EventHandler
	public void onPlayerInteractListener(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getItem() == null) {
			return;
		} else if (event.getItem().getTypeId() == 331 || event.getItem().getTypeId() == 264) {
			// Redstone/diamond
			Player player = event.getPlayer();
			Racer racer = this.plugin.getPlayerToRacer().get(player);
			int index = racer.getRace().getPlayerPositions().indexOf(racer);
			if (index == 0) {
				player.getInventory().clear(); // remove item
			} else {
				if (event.getItem().getTypeId() == 331) {
					index -= 1;
				} else if (event.getItem().getTypeId() == 264) {
					index = 0;
				}
				Racer target = racer.getRace().getPlayerPositions().get(index);
				if (event.getItem().getTypeId() == 331) {
					this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new RedShellDelayTask(this.plugin, target), 100);
				} else if (event.getItem().getTypeId() == 264) {
					this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new BlueShellDelayTask(this.plugin, target), 100);
				}	
				player.getInventory().clear(); // remove item
			}
			event.setCancelled(true);
		} else if (event.getItem().getTypeId() == 46) {
			// tnt
			Player player = event.getPlayer();
			Racer racer = this.plugin.getPlayerToRacer().get(player);
			// drop item
			if (player.getItemInHand().getTypeId() == 46) {
				Item item = player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(player.getItemInHand().getType(), 1));
				racer.getRace().getItemsOnTrack().add(item);
				player.getInventory().clear();
			}
		}
	}

}
