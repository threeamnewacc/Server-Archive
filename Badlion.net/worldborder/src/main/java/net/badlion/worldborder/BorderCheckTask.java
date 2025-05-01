package net.badlion.worldborder;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;


public class BorderCheckTask implements Runnable
{
    public static Map<UUID, Location> lastValidLocationMap = new HashMap<>();

	@Override
	public void run()
	{
		// if knockback is set to 0, simply return
		if (Config.KnockBack() == 0.0)
			return;

		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			checkPlayer(p, null, false, true);
		}
	}

	// set targetLoc only if not current player location; set returnLocationOnly to true to have new Location returned if they need to be moved to one, instead of directly handling it
	public static Location checkPlayer(Player player, Location targetLoc, boolean returnLocationOnly, boolean notify)
	{
		if (player == null) return null;

		Location loc = (targetLoc == null) ? player.getLocation().clone() : targetLoc;
		if (loc == null) return null;

		World world = loc.getWorld();
		if (world == null) return null;
		BorderData border = Config.Border(world.getName());
		if (border == null) return null;

        // GStart - Store last valid locations
		if (border.insideBorder(loc.getX(), loc.getZ(), Config.ShapeRound())) {
            lastValidLocationMap.put(player.getUniqueId(), player.getLocation());
			return null;
        }
        // GEnd

		// if player is in bypass list (from bypass command), allow them beyond border; also ignore players currently being handled already
		if (Config.isPlayerBypassing(player.getName()))
			return null;

        // GStart - Diff tracking for last valid location
		Location newLoc = lastValidLocationMap.get(player.getUniqueId());

		// If the location doesn't exist or they are not in the border with their old location or they are changing worlds
        if (newLoc == null || !border.insideBorder(newLoc.getX(), newLoc.getZ(), Config.ShapeRound()) || !newLoc.getWorld().getName().equals(loc.getWorld().getName())) {
			newLoc = newLocation(player, loc, border, notify);
			lastValidLocationMap.remove(player.getUniqueId());
        }
        // GEnd

		// Give some particle and sound effects where the player was beyond the border, if "whoosh effect" is enabled
		Config.showWhooshEffect(loc);

		if (!returnLocationOnly) {
			// GStart
			Gberry.safeTeleport(player, newLoc);
			// GEnd
		}

		if (returnLocationOnly)
			return newLoc;

		return null;
	}
	public static Location checkPlayer(Player player, Location targetLoc, boolean returnLocationOnly)
	{
		return checkPlayer(player, targetLoc, returnLocationOnly, true);
	}

	private static Location newLocation(Player player, Location loc, BorderData border, boolean notify)
	{
		if (Config.Debug())
		{
			Config.logWarn((notify ? "Border crossing" : "Check was run") + " in \"" + loc.getWorld().getName() + "\". Border " + border.toString());
			Config.logWarn("Player position X: " + Config.coord.format(loc.getX()) + " Y: " + Config.coord.format(loc.getY()) + " Z: " + Config.coord.format(loc.getZ()));
		}

		Location newLoc = border.correctedPosition(loc, Config.ShapeRound(), player.isFlying());

		// it's remotely possible (such as in the Nether) a suitable location isn't available, in which case...
		if (newLoc == null)
		{
			if (Config.Debug())
				Config.logWarn("Target new location unviable, using spawn or killing player.");
			if (Config.getIfPlayerKill())
			{
				player.setHealth(0.0D);
				return null;
			}

			// GStart - Recursive calls \o/
			Random random = new Random();
			int radius = WorldBorder.plugin.GetWorldBorder(loc.getWorld().getName()).getRadiusX();
			int randX = random.nextInt((radius * 2) + 1) + -radius;
			int randZ = random.nextInt((radius * 2) + 1) + -radius;

			loc = loc.getWorld().getHighestBlockAt(randX, randZ).getLocation();
			newLoc = newLocation(player, loc, border, false);//player.getWorld().getSpawnLocation();
			// GEnd
		}

		if (Config.Debug())
			Config.logWarn("New position in world \"" + newLoc.getWorld().getName() + "\" at X: " + Config.coord.format(newLoc.getX()) + " Y: " + Config.coord.format(newLoc.getY()) + " Z: " + Config.coord.format(newLoc.getZ()));

		if (notify)
			player.sendMessage(Config.Message());

		return newLoc;
	}

}
