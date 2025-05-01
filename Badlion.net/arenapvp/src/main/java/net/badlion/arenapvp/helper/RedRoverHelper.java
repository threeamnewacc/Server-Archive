package net.badlion.arenapvp.helper;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.matchmaking.RedRoverBattle;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RedRoverHelper {

	public static void giveCaptainItems(Player captain, Team team) {
		captain.getInventory().clear();

		for (Player player : team.getActivePlayers()) {
			if (player.equals(captain)) {
				continue;
			}
			captain.getInventory().addItem(ItemStackUtil.createItem(Material.EYE_OF_ENDER, ChatColor.GREEN + player.getDisguisedName()));
		}
	}


	// Spawns in a cool curve effect from the players location to the target location (Target location will be the warp for the arena),
	// will be used when a team captain in red rover picks a fighter, once the effect makes it to the target we should spawn in the fighter
	public static void launchFighterEffect(Player player, Location target, RedRoverBattle match, Player fighter) {
		double launchX = player.getEyeLocation().getX();
		double launchZ = player.getEyeLocation().getZ();
		double launchY = player.getEyeLocation().getY();

		double targetX = target.getX();
		double targetZ = target.getZ();
		double targetY = target.getY();

		double distanceX = Math.abs(launchX - targetX);
		double distanceZ = Math.abs(launchZ - targetZ);

		double controlPointX;
		double controlPointZ;

		//The curve will be sharpest near the target point
		if (distanceX < distanceZ) {
			controlPointX = launchX;
			controlPointZ = launchZ > targetZ ? launchZ - distanceZ : launchZ + distanceZ;
		} else {
			controlPointX = launchX > targetX ? launchX - distanceX : launchX + distanceX;
			controlPointZ = launchZ;
		}

		// Control point y is in between the two points
		double controlPointY = Math.min(launchY, targetY) + (Math.abs(launchY - targetY) / 2);

		int i = 0;
		for (double t = 0.0; t <= 1; t += 0.01) {
			// Quadratic Bézier curve equation for x, y, and z points
			double x = ((1 - t) * (1 - t) * launchX + 2 * (1 - t) * t * controlPointX + t * t * targetX);
			double y = ((1 - t) * (1 - t) * launchY + 2 * (1 - t) * t * controlPointY + t * t * targetY);
			double z = ((1 - t) * (1 - t) * launchZ + 2 * (1 - t) * t * controlPointZ + t * t * targetZ);
			Location lineLocation = new Location(target.getWorld(), x, y, z);
			final double finalT = t;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (finalT == 1) {
						match.spawnInPlayerToWarp(fighter);
					}
					target.getWorld().playEffect(lineLocation, Effect.HAPPY_VILLAGER, 0);
				}
			}.runTaskLater(ArenaPvP.getInstance(), i++ / 2);
		}
	}
}
