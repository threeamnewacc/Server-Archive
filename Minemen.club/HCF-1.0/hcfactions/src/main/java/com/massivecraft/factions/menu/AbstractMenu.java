package com.massivecraft.factions.menu;

import club.minemen.hcfactions.HCFactions;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class AbstractMenu implements InventoryHolder {

	@Getter
	protected final HCFactions plugin;

	@Getter
	protected final Inventory inventory;

	public AbstractMenu(HCFactions plugin, int size, String title) {
		this.plugin = plugin;
		if (title.length() > 32) {
			title = title.substring(0, 32);
		}
		this.inventory = plugin.getServer().createInventory(this, size, title);
	}

	public void open(Player player) {
		player.openInventory(inventory);
	}

	public abstract void onInventoryClick(InventoryClickEvent event);

	public abstract void onInventoryDrag(InventoryDragEvent event);

	public abstract void onInventoryClose(InventoryCloseEvent event);

}
