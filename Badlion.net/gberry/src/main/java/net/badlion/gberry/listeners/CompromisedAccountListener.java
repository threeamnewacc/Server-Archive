package net.badlion.gberry.listeners;

import net.badlion.gberry.Gberry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class CompromisedAccountListener implements Listener {
	
	private Gberry plugin;

   	public CompromisedAccountListener(Gberry plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(final AsyncPlayerPreLoginEvent event) {
		// TODO: FIX THESE ACCOUNTS!!
		if (event.getName().toLowerCase().startsWith("idminecraft")) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Compromised accounts are not allowed.");
		} else if (event.getName().toLowerCase().contains("badlion") && !event.getName().equals("Badlion") && !event.getName().equals("iBadlion")) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You have an invalid username, change it to play on our servers.");
		}
	}

}
