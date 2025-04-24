package com.massivecraft.factions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.Random;

public class FactionsVehicleListener implements Listener {

	private static final Random random = new Random();

	public static Location getNearbyWilderness(Player player) {
		FLocation fLocation = Board.getClosestNonClaimed(new FLocation(player.getLocation()));
		if (fLocation == null) {
			return null;
		}
		Chunk chunk = fLocation.getWorld().getChunkAt((int) fLocation.getX(), (int) fLocation.getZ());
		int groundLvl = 70;
		Location closest = null;
		for (int i = 0; i < 16; i++) {
			Location chunkLoc = new Location(chunk.getWorld(), (chunk.getX() << 4) + random.nextInt(16), 0, (chunk.getZ() << 4) + random.nextInt(16));
			Location highestChunkLoc = chunk.getWorld().getHighestBlockAt(chunkLoc).getLocation();
			double distance = Math.abs(highestChunkLoc.getY() - groundLvl);
			if (highestChunkLoc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
				if (closest != null) {
					if (Math.abs(closest.getY() - groundLvl) > distance) {
						closest = highestChunkLoc;
					}
				} else {
					closest = highestChunkLoc;
				}
			}
		}
		if (closest != null) {
			return closest.add(0.5, 0, 0.5);
		}
		return null;
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (event.getVehicle() instanceof Horse) {
			return;
		}
		if (!(event.getEntered() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntered();
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);

		if (factionPlayer.isAdminBypassing()) {
			return;
		}

		Faction myFaction = factionPlayer.getFaction();
		Entity vehicle = event.getVehicle();

		Location loc = vehicle.getLocation();

		int x = loc.getBlockX() >> 4;
		int z = loc.getBlockZ() >> 4;

		Faction here = Board.getFactionAt(new FLocation(loc.getWorld().getName(), x, z));
		if (here != myFaction && here.isNormal()) {
			event.setCancelled(true);
			player.sendFormattedMessage("{0}You can''t enter a vehicle in faction land.", ChatColor.RED);
			return;
		}

		boolean factionNear = false;
		for (int dx = -Conf.landBorder; dx <= Conf.landBorder; dx++) {
			for (int dz = -Conf.landBorder; dz <= Conf.landBorder; dz++) {
				here = Board.getFactionAt(new FLocation(loc.getWorld().getName(), x + dx, z + dz));
				if (here != myFaction && here.isNormal()) {
					factionNear = true;
				}
			}
		}
		if (factionNear) {
			event.setCancelled(true);
			player.sendFormattedMessage("{0}You can''t enter a vehicle here: faction land is nearby.", ChatColor.RED);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!event.getPlayer().isInsideVehicle()) {
			return;
		}
		Entity vehicle = event.getPlayer().getVehicle();
		if (vehicle instanceof Horse) {
			return;
		}

		Player player = event.getPlayer();
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);

		if (factionPlayer.isAdminBypassing()) {
			return;
		}

		Faction myFaction = factionPlayer.getFaction();
		Location loc = vehicle.getLocation();

		int x = loc.getBlockX() >> 4;
		int z = loc.getBlockZ() >> 4;

		Faction here = Board.getFactionAt(new FLocation(loc.getWorld().getName(), x, z));
		if (here != myFaction && here.isNormal()) {
			player.sendFormattedMessage("{0}You can''t use vehicles in faction land.", ChatColor.RED);
			Location nearbyWilderness = getNearbyWilderness(player);
			if (nearbyWilderness == null) {
				vehicle.eject();
			} else {
				player.teleport(nearbyWilderness);
			}
		}
	}
}
