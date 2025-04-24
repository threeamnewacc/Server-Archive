package com.massivecraft.factions.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LeafListener implements Listener {

	@EventHandler
	public void onBlockDecay(LeavesDecayEvent event) {
		// More apples
		if (new Random().nextInt(20) == 1) {
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
		}
	}
}
