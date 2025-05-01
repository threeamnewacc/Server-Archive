package net.badlion.gberry.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.tinywebteam.badlion.MineKart;

public class HungerListener implements Listener {
	
	private MineKart plugin;
	
	public HungerListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onHungerLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

}
