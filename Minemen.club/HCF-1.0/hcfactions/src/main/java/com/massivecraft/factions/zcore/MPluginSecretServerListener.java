package com.massivecraft.factions.zcore;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

@RequiredArgsConstructor
public class MPluginSecretServerListener implements Listener {

	private final MPlugin p;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerCommand(ServerCommandEvent event) {
		if (event.getCommand().length() == 0) {
			return;
		}

		if (p.handleCommand(event.getSender(), event.getCommand())) {
			event.setCommand(p.refCommand);
		}
	}

}
