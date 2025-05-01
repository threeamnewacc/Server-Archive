package net.badlion.gberry.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.tinywebteam.badlion.MineKart;


public class ItemDropListener implements Listener {

	private MineKart server;
	
	public ItemDropListener(MineKart server){
		this.server = server;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerItemDrop(PlayerDropItemEvent event){
		event.getItemDrop().remove();
	}
	
}
