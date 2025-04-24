package com.massivecraft.factions.type;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class HomeTask {

	private final HCFactions plugin;
	private final Player player;
	private final BukkitTask task;
	private final Location location;

	@Getter
	private int countdown = 10;

	public HomeTask(HCFactions plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
		location = player.getLocation();
		task = Bukkit.getScheduler().runTaskTimer(plugin, this::onTimer, 0, 20);
	}

	public static void doEffect(Location loc) {
		for (int i = 0; i < 20; i++) {
			loc.getWorld().spigot().playEffect(loc, Effect.ENDER_SIGNAL, 0, 0, 0, 0, 0, 1, 1, 24);
		}
		loc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
	}

	public void cancel() {
		task.cancel();
		plugin.getHomeTasks().remove(player);
	}

	private void onTimer() {
		if (player.isDead() || !player.isOnline()) {
			this.cancel();
			return;
		}
		if (countdown == 0) {
			Faction faction = FPlayers.getInstance().get(player).getFaction();
			if (faction != null && faction.hasHome()) {
				faction.getHome().getChunk().load();
				player.teleport(faction.getHome());
				doEffect(faction.getHome().clone().add(0, 1, 0));
				player.sendFormattedMessage("{0}Teleported home.", ChatColor.GREEN);
			}
			cancel();
			return;
		}
		Location newLocation = player.getLocation();
		if (newLocation.getWorld() != location.getWorld() || newLocation.distanceSquared(location) > 1.0) {
			cancel();
			player.sendFormattedMessage("{0}Teleporting home cancelled because you moved.", ChatColor.RED);
			return;
		}
		player.sendFormattedMessage("{0}Teleporting to home in {1} seconds.", ChatColor.GOLD, ChatColor.YELLOW + String.valueOf(countdown) + ChatColor.GOLD);
		player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
		countdown--;
	}
}
