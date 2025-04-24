package com.massivecraft.factions.zcore;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@RequiredArgsConstructor
public class MPluginSecretPlayerListener implements Listener {

	private final MPlugin plugin;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			return;
		}
		FPlayers.getInstance().login(event.getPlayer());
		for (EntityCollection<? extends Entity> entityCollection : EM.entityClassMap.values()) {
			if (entityCollection instanceof PlayerEntityCollection) {
				((PlayerEntityCollection) entityCollection).get(event.getPlayer());
			}
		}
	}
}
