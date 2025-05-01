package net.badlion.uhc.util;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ScatterUtils {

    public static ArrayList<Location> randomSquareScatter(int pCount) {
        int radius = (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() - 25;

        return randomSquareScatter(pCount, radius, 5);
    }

    public static ArrayList<Location> randomSquareScatter(int pCount, int radius, int y) {
        System.out.println("pcount " + pCount);
        Random randy = new Random();

        ArrayList<Location> locations = new ArrayList<>();

        //double minDistance = (radius * 2 - 100D) / pCount;
        double minDistance = 20D; // Solves issues with small player counts
        World world = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);

        for (int i = 0; i < pCount; i++) {
	        boolean goodSpawnPointFound = false;
            Location scatterPoint = new Location(world, 0.0D, 0.0D, 0.0D);
	        Location backupLocation = null;
            for (int k = 0; k < 100; k++) {
                double d1 = randy.nextDouble() * radius * 2.0D - radius;
                double d2 = randy.nextDouble() * radius * 2.0D - radius;
                d1 = Math.round(d1) + 0.5D;
                d2 = Math.round(d2) + 0.5D;
                scatterPoint.setX(d1);
                scatterPoint.setZ(d2);
                scatterPoint.setY(world.getHighestBlockYAt(scatterPoint) + y);

	            if (ScatterUtils.isLocationBlockValid(scatterPoint)) {
		            // Set backup spawn location
		            if (backupLocation == null) backupLocation = scatterPoint;

		            if (ScatterUtils.isLocationValid(scatterPoint, locations, minDistance)) {
			            goodSpawnPointFound = true;
			            break;
		            }
	            }
            }
            if (!goodSpawnPointFound) {
	            scatterPoint = backupLocation;
	            Bukkit.getLogger().log(Level.WARNING, "MaxAttemptsReachedException"); // Didn't feel like making an exception
            }
            locations.add(scatterPoint);
        }
        return locations;
    }

	public static ArrayList<Location> randomSquareScatterFromPoints(int pCount, int maxX, int maxZ, int minX, int minZ, int y) {
		Random randy = new Random();

		ArrayList<Location> locations = new ArrayList<>();

		double minDistance = 20D;
		World world = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);

		for (int i = 0; i < pCount; i++) {
			boolean goodSpawnPointFound = false;
			Location scatterPoint = new Location(world, 0.0D, 0.0D, 0.0D);
			Location backupLocation = null;
			for (int k = 0; k < 100; k++) {
				int randX = randy.nextInt(maxX - minX + 1) + minX;
				int randZ = randy.nextInt(maxZ - minZ + 1) + minZ;
				scatterPoint.setX(randX);
				scatterPoint.setZ(randZ);
				scatterPoint.setY(world.getHighestBlockYAt(scatterPoint) + y);

				if (ScatterUtils.isLocationBlockValid(scatterPoint)) {
					// Set backup spawn location
					if (backupLocation == null) {
						backupLocation = scatterPoint;
					}

					if (ScatterUtils.isLocationValid(scatterPoint, locations, minDistance)) {
						goodSpawnPointFound = true;
						break;
					}
				}
			}

			if (!goodSpawnPointFound) {
				scatterPoint = backupLocation;
				Bukkit.getLogger().log(Level.WARNING, "MaxAttemptsReachedException"); // Didn't feel like making an exception
			}
			locations.add(scatterPoint);
		}

		return locations;
	}

    public static void scatterTeams(Set<UHCTeam> uhcTeams, int radius) {
        for (UHCTeam uhcTeam : uhcTeams) {
            Location location = ScatterUtils.randomSquareScatter(1, radius, 1).get(0);

            for (UUID uuid : uhcTeam.getUuids()) {
                Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);
                if (player != null) {
                    UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
                    if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
                        Gberry.safeTeleport(player, location);
                    }
                }
            }
        }
    }

    private static boolean isLocationValid(Location location, ArrayList<Location> locations, Double d) {
        for (Location loc : locations) {
            if (Math.sqrt(NumberConversions.square(loc.getX() - location.getX()) + NumberConversions.square(loc.getZ() - location.getZ())) < d) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLocationBlockValid(Location loc) {
        Material type = loc.getBlock().getRelative(0, -6, 0).getType();
        //System.out.println("Block Location: " + loc.getBlock().getRelative(0, -6, 0).getLocation());
        //System.out.println("Block Type: " + loc.getBlock().getRelative(0, -6, 0).getType());
	    return !(type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.WATER || type == Material.STATIONARY_WATER);
    }

    public static double getXFromRadians(double d1, double d2) {
        return Math.round(d1 * Math.sin(d2)) + 0.5D;
    }

    public static double getZFromRadians(double d1, double d2) {
        return Math.round(d1 * Math.cos(d2)) + 0.5D;
    }

}
