package com.massivecraft.factions.type;

import club.minemen.clublibrary.util.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class StuckTask {
	private final HCFactions plugin;
	private final Player player;
	private final BukkitTask task;
	private final Location location;

	@Getter
	private int countdown = 180;

	public StuckTask(HCFactions plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
		location = player.getLocation();
		task = Bukkit.getScheduler().runTaskTimer(plugin, this::onTimer, 0, 20);
	}

	public void cancel() {
		task.cancel();
		plugin.getStuckTasks().remove(player);
	}

	private void onTimer() {
		if (player.isDead() || !player.isOnline()) {
			this.cancel();
			return;
		}
		if (countdown == 0) {
			FLocation fLocation = Board.getClosestNonClaimed(new FLocation(player.getLocation()));
			if (fLocation == null) {
				player.sendFormattedMessage("{0}Unable to find a safe spot to teleport you to.", CC.RED);
				return;
			}
			Chunk chunk = fLocation.getWorld().getChunkAt((int) fLocation.getX(), (int) fLocation.getZ());
			int groundLvl = 70;
			Location closest = null;
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					Location chunkLoc = new Location(chunk.getWorld(), (chunk.getX() << 4) + x, 0, (chunk.getZ() << 4) + z);
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
			}
			if (closest != null) {
				player.teleport(closest.add(0.5, 0, 0.5));
				player.sendFormattedMessage("{0}You''ve been teleported out of that claim.", CC.PRIMARY);
			} else {
				player.sendFormattedMessage("{0}Unable to find a safe spot to teleport you to.", CC.RED);
			}
			cancel();
			return;
		}
		Location newLocation = player.getLocation();
		if (newLocation.getWorld() != location.getWorld() || newLocation.distanceSquared(location) > 1.0) {
			cancel();
			player.sendFormattedMessage("{0}Faction stuck was cancelled because you moved.", CC.RED);
			return;
		}
		if (countdown % 5 == 0) {
			player.sendFormattedMessage("{0}You''ll be teleported out of the claim in {1}{2}{0} seconds.", CC.PRIMARY, CC.SECONDARY, String.valueOf(countdown));
			player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
		}
		countdown--;
	}
}
