package net.badlion.smellylobby.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class LobbyListener implements Listener {

	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}

}
