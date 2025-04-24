package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.entity.Player;

public class DtrTask implements Runnable {

	private long lastRun = System.currentTimeMillis();

	@Override
	public void run() {
		long now = System.currentTimeMillis();
		long millisDiff = now - lastRun;
		double minutesDiff = millisDiff / 60000.0;

		for (Faction faction : Factions.getInstance().getAll()) {
			if (faction.getDtrRegenCooldown() > now) {
				continue;
			}
			if (isOnlineAndAlive(faction)) {
				faction.alterDtr(Conf.dtrRegenRateOnline * minutesDiff);
			} else {
				faction.alterDtr(Conf.dtrRegenRateOffline * minutesDiff);
			}
		}

		lastRun = now;
	}

	private boolean isOnlineAndAlive(Faction faction) {
		for (FactionPlayer fplayer : faction.getFPlayers()) {
			Player player = fplayer.getPlayer();
			if (player != null && player.getHealth() > 0) {
				return true;
			}
		}
		return false;
	}
}
