package net.badlion.gfactions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class StuckCommand implements CommandExecutor {

	private Map<Player, StuckLocationCheckerTask> playersUnstucking = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = ((Player) sender);
			if (this.playersUnstucking.containsKey(player)) {
				player.sendMessage(ChatColor.YELLOW + "You are in the process of being teleported, please wait!");
			} else if (Board.getFactionAt(player.getLocation()) != FPlayers.i.get(player).getFaction()) {
				player.sendMessage(ChatColor.GOLD + "Teleportation will commence in " + ChatColor.RED + "3 minutes"
						+ ChatColor.GOLD + ". Don't move.");

				StuckLocationCheckerTask task = new StuckLocationCheckerTask(player);
				task.runTaskTimer(GFactions.plugin, 20L, 20L);
				this.playersUnstucking.put(player, task);
			} else {
				player.sendMessage(ChatColor.YELLOW + "You are not stuck!");
			}
		}
		return true;
	}

	private class StuckLocationCheckerTask extends BukkitRunnable {

		private Player player;
		private Location location;

		private int secondsLeft = 180;

		public StuckLocationCheckerTask(Player player) {
			this.player = player;
			this.location = player.getLocation();
		}

		@Override
		public void run() {
			// Did they move?
			Location newLocation = this.player.getLocation();
			if (newLocation.getBlockX() != this.location.getBlockX()
					|| newLocation.getBlockY() != this.location.getBlockY()
					|| newLocation.getBlockZ() != this.location.getBlockZ()) {
				this.player.sendMessage(ChatColor.RED + "You moved in the 3 minute grace period. " +
						"The teleportation request has been cancelled.");

				this.cancel();
				return;
			}

			this.secondsLeft--;

			if (this.secondsLeft == 0) {
				// Find an unclaimed area near the player
				int counter = 0;
				Location teleportLocation = null;
				while (teleportLocation == null) {
					teleportLocation = this.randomNearbyLocation(this.player);

					counter++;

					// Too many failed attempts?
					if (counter == 100) {
						this.player.sendMessage(ChatColor.YELLOW + "Teleporting...");
						this.player.teleport(GFactions.plugin.getSpawnLocation());

						StuckCommand.this.playersUnstucking.remove(this.player);
						this.cancel();
						return;
					}
				}
				this.player.sendMessage(ChatColor.YELLOW + "Teleporting...");
				this.player.teleport(teleportLocation);

				StuckCommand.this.playersUnstucking.remove(this.player);
				this.cancel();
			}
		}

		private Location randomNearbyLocation(Player player) {
			Location location = player.getLocation();
			int x = GFactions.plugin.generateRandomInt(100, 700);
			int z = GFactions.plugin.generateRandomInt(100, 700);

			// Negatives
			if (x < 400) {
				x = -x;
			} else {
				x = x - 300;
			}
			if (z < 400) {
				z = -x;
			} else {
				z = z - 300;
			}

			int y = location.getWorld().getHighestBlockYAt(location.getBlockX() + x, location.getBlockZ() + z);

			Location newLocation = new Location(location.getWorld(), location.getX() + x, y, location.getZ() + z);

			// Don't spawn in water/lava
			Material material = newLocation.add(0, -1, 0).getBlock().getType();
			if (material == Material.STATIONARY_WATER || material == Material.WATER
					|| material == Material.LAVA || material == Material.STATIONARY_LAVA) {
				return null;
			}

			Faction faction = Board.getFactionAt(newLocation);

			if (faction.getId().equals("0")) {
				return newLocation.add(0, 4, 0); // Up a bit more
			}

			return null;
		}

	}

}
