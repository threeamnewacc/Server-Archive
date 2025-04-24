package com.massivecraft.factions.listeners;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.DeathbanEvent;
import com.massivecraft.factions.event.ProtectionTimeCountdownEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KtagListener implements Listener {

	@EventHandler
	public void onProtectionTimeCountdown(ProtectionTimeCountdownEvent event) {
		Faction factionHere = Board.getFactionAt(new FLocation(event.getPlayer().getLocation()));
		if (factionHere.noPvPInTerritory()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeathban(DeathbanEvent event) {
		if (HCFactions.getInstance().endOfTheWorld) {
			// Day deathban for eotw
			event.setDeathbanTime((long) 86400);
			return;
		}
		if (Conf.betrayerDeathbanMultiplier > 1.0) {
			if (event.getPlayer().hasPermission("betrayer")) {
				event.setDeathbanTime((long) (event.getDeathbanTime() * Conf.betrayerDeathbanMultiplier));
				HCFactions.getInstance().getLogger().info(event.getPlayer().getName() + " gets betrayer deathban multiplier - x" + Conf.betrayerDeathbanMultiplier);
			}
		}
	}
}
