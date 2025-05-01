package net.badlion.gberry.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import com.tinywebteam.badlion.MineKart;

public class PlayerItemListener implements Listener {

	private MineKart server;
	
	public PlayerItemListener(MineKart server){
		this.server = server;
	}
	
	
	//cancel opening inventory
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryOpen(InventoryClickEvent event) {
		HumanEntity human = event.getView().getPlayer();
		if (human instanceof Player) {
			Player player = (Player) human;
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "You cannot edit your inventory.");
				human.closeInventory();
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryDrag(InventoryDragEvent event) {
		HumanEntity human = event.getView().getPlayer();
		if (human instanceof Player) {
			Player player = (Player) human;
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "You cannot edit your inventory.");
				human.closeInventory();
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void onItemPickupEvent(InventoryPickupItemEvent event) {
		event.setCancelled(true);
	}
	
	
	
	
	
}
