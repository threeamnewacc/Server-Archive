package com.massivecraft.factions.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.FPlayers;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PvpProtectionManager {

	private final Cache<Location, Long> lootProtectedAreas = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();


	public String getProtectionTimeString(Player player) {
		return DurationFormatUtils.formatDurationWords(FPlayers.getInstance().get(player).getPvpProtectionTime(), true, true);
	}

	public void addLootProtectArea(Location location) {
		lootProtectedAreas.put(location, System.currentTimeMillis());
	}

	public boolean isLocationLootProtected(Location location) {
		for (Location area : lootProtectedAreas.asMap().keySet()) {
			if (area.getWorld() == location.getWorld()) {
				if (area.distanceSquared(location) < 10 * 10) {
					// within 10 blocks of loc?
					return true;
				}
			}
		}
		return false;
	}

}
