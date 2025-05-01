package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.tasks.manhunt.ManHuntBoundsCheckerTask;
import net.badlion.gberry.Gberry;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.Timestamp;
import java.util.Date;

public class ManHuntCommand implements CommandExecutor {

	private GFactions plugin;

	public ManHuntCommand(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (args.length == 1 && args[0].equalsIgnoreCase("start") && sender.isOp()) {
            /*List<String> chests = (List<String>) this.plugin.getConfig().getList("gfactions.man_hunt.pp_names");
            String chestName = chests.get(this.plugin.generateRandomInt(0, chests.size() - 1));

            int x = this.plugin.getConfig().getInt("gfactions.man_hunt.pp_locations." + chestName + ".pp_x");
            int y = this.plugin.getConfig().getInt("gfactions.man_hunt.pp_locations." + chestName + ".pp_y");
            int z = this.plugin.getConfig().getInt("gfactions.man_hunt.pp_locations." + chestName + ".pp_z");*/

			// Is a Man Hunt already running?
			if (this.plugin.getManHuntPP() != null) {
				sender.sendMessage(ChatColor.RED + "A Man Hunt is already running!");
				return true;
			}

			// Get a good location
			Location location = null;
			while (location == null) {
				Location tempLoc = this.getNiceManHuntLocation();
				if (tempLoc != null) {
					location = tempLoc;
				}
			}

			int x = (int) location.getX();
			int y = (int) location.getY();
			int z = (int) location.getZ();

			// Create the pressure plate
			Block block = this.plugin.getServer().getWorld("world").getBlockAt(x, y, z);
			block.setType(Material.STONE_PLATE);

			// Keep track of the pressure plate
			this.plugin.setManHuntPP(block);
			new ManHuntBoundsCheckerTask(this.plugin).runTaskTimer(this.plugin, 2L, 2L); // Sneak sneaky eh little poopy?

			Gberry.broadcastMessage(ChatColor.GREEN + "A new Man Hunt has started in the warzone at " + x + ", " + y + ", " + z + "!");
			Gberry.broadcastMessage(ChatColor.GREEN + "Whoever steps on the pressure plate first must stay alive for 10 continuous minutes without being able to logout or leave the warzone." +
					"This player's coordinates will be broadcasted every minute.");

			// Call TabList event
			EventStateChangeEvent event = new EventStateChangeEvent("Man Hunt", true);
			this.plugin.getServer().getPluginManager().callEvent(event);
		} else {
			sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
			Timestamp currentTime = new java.sql.Timestamp(new Date().getTime());
			sender.sendMessage(ChatColor.GOLD + "Man Hunt times are shown below:");
			sender.sendMessage(ChatColor.RED + "Current time - " + currentTime.getHours() + ":" + currentTime.getMinutes());
			for (String dateTime : this.plugin.getConfig().getStringList("gfactions.man_hunt.man_hunt_times")) {
				sender.sendMessage(ChatColor.AQUA + " - " + dateTime + " Eastern");
			}
		}
		return true;
	}

    private Location getNiceManHuntLocation() {
        World w = this.plugin.getServer().getWorld("world");
        int x = this.plugin.generateRandomInt(this.plugin.getWarZoneMinX(), this.plugin.getWarZoneMaxX());
        int z = this.plugin.generateRandomInt(this.plugin.getWarZoneMinZ(), this.plugin.getWarZoneMaxZ());
        int y = w.getHighestBlockYAt(x, z);

        Material type = w.getBlockAt(x, y, z).getRelative(0, -1, 0).getType();
        if (!type.isSolid()) {
            return null;
        }

        // Go through the 16x16 grid and make sure nothing is in the zone (stick to the edges)
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                // We don't care about the middle
                if (i > 0 && i < 15)  {
                    continue;
                }

                ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(this.plugin.getServer().getWorld("world").getBlockAt(x, y, z).getLocation(),
                        this.plugin.getgGuardPlugin().getProtectedRegions());
                if (region != null && !region.getRegionName().equals("warzone")) {
                    return null; // failed, we are in another zone, do it again
                }

                // Cool this block belongs to the war zone, keep going
            }
        }
        return new Location(w, x, y, z); // ez location
    }

}