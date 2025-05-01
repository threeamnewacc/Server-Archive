package net.badlion.gberry.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {
	
	public ChunkListener() {
		
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}

}
