package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.commands.DoubleOresCommand;
import net.badlion.gfactions.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventsCommand implements CommandExecutor {

	private GFactions plugin;
	
	public EventsCommand(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			boolean somethingIsGoingOn = false;
			
			// KOTH?
			if (this.plugin.getKoth() != null) {
				KOTH koth = this.plugin.getKoth();
				somethingIsGoingOn = true;
				if (koth.getScoreTrackerTask() != null) {
					player.sendMessage(ChatColor.GREEN + "The KOTH \"" + koth.getKothName() + "\" is currently going on right now with Cap Zone Location: (" + koth.getCapzoneLocation1().getX() + ", " + koth.getCapzoneLocation1().getY() + ", " + koth.getCapzoneLocation1().getZ() + ") to (" + koth.getCapzoneLocation2().getX() + ", " + koth.getCapzoneLocation2().getY() + ", " + koth.getCapzoneLocation2().getZ() + ").");
				} else {
					player.sendMessage(ChatColor.GREEN + "A KOTH will be starting within 10 minutes at (" + koth.getCapzoneLocation1().getX() + ", " + koth.getCapzoneLocation1().getY() + ", " + koth.getCapzoneLocation1().getZ() + ")");
				}
			}
			
			// Dungeon?
            if (this.plugin.getDungeonManager().isPortalEvent()) {
                player.sendMessage(ChatColor.GREEN + "A Dungeon Portal is going to spawn within 10 minutes at: " + this.plugin.getDungeonManager().getLocation1().getBlockX() + ", " + this.plugin.getDungeonManager().getLocation1().getBlockY() + ", " + this.plugin.getDungeonManager().getLocation1().getBlockZ());
            } else if (this.plugin.getDungeonManager().isAllowEntry()) {
				somethingIsGoingOn = true;
				player.sendMessage(ChatColor.GREEN + "There is a Dungeon going on right now with Portal Location: " + this.plugin.getDungeonManager().getLocation1().getBlockX() + ", " + this.plugin.getDungeonManager().getLocation1().getBlockY() + ", " + this.plugin.getDungeonManager().getLocation1().getBlockZ());
			}

            // Tower?
			/*if (this.plugin.getCheckTimeTask().isTowerWarning() && this.plugin.getTower() == null) {
				somethingIsGoingOn = true;
				player.sendMessage(ChatColor.GREEN + "An Insanity Tower will spawn within 5 minutes at (" + this.plugin.getCheckTimeTask().getxTowerLocation() + ", " + this.plugin.getCheckTimeTask().getyTowerLocation() + ", " + this.plugin.getCheckTimeTask().getzTowerLocation() + ")");
			} else if (this.plugin.getTower() != null) {
                somethingIsGoingOn = true;
                player.sendMessage(ChatColor.GREEN + "There is a Tower event going on right now with Location: " + this.plugin.getTower().getBottomLeftCornerLocation().getBlockX() + ", " + this.plugin.getTower().getBottomLeftCornerLocation().getBlockY() + ", " + this.plugin.getTower().getBottomLeftCornerLocation().getBlockZ());
            }*/

            // Manhunt
			if (this.plugin.getManHuntPP() != null) {
				somethingIsGoingOn = true;
				Location location = this.plugin.getManHuntPP().getLocation();
				player.sendMessage(ChatColor.GREEN + "A Man Hunt has started in the warzone at " + location.getX() + ", " + location.getY() + ", " + location.getZ() + "!");
                if (this.plugin.getManHuntTagged() == null) {
                    player.sendMessage(ChatColor.GREEN + "- No one has been tagged yet!");
                } else {
                    Location loc = this.plugin.getManHuntTagged().getLocation();
                    player.sendMessage(ChatColor.GREEN + "- " + this.plugin.getManHuntTagged().getName() + " is tagged and is currently at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
                }
			}

			// Parkour
            if (this.plugin.getParkourChest() != null) {
                somethingIsGoingOn = true;
                player.sendMessage(ChatColor.GREEN + "There is a parkour chest at " + this.plugin.getParkourChest().getX() + ", " + this.plugin.getParkourChest().getY() + ", " + this.plugin.getParkourChest().getZ());
            }

			// Bloodbowl
			if (this.plugin.getBloodBowlManager().isRunning()) {
				somethingIsGoingOn = true;
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry1().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry1().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry1().getBlockZ());
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry2().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry2().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry2().getBlockZ());
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry3().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry3().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry3().getBlockZ());
				player.sendMessage(ChatColor.GREEN + "There is a BloodBowl portal located at " + this.plugin.getBloodBowlManager().getLowerBoundEntry4().getBlockX() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry4().getBlockY() + ", "
										   + this.plugin.getBloodBowlManager().getLowerBoundEntry4().getBlockZ());
			}

			// Scavenge
			if (this.plugin.getScavenge() != null) {
				somethingIsGoingOn = true;
				player.sendMessage(ChatColor.GREEN + "A scavenge event is going on. There are chests scattered in the WarZone full of loot!");
			}

			// Double Ores
			if (DoubleOresCommand.doubleOresActivated) {
				somethingIsGoingOn = true;
				player.sendMessage(ChatColor.GREEN + "Double ores is active!");
			}

			if (!somethingIsGoingOn) {
				player.sendMessage(ChatColor.RED + "No events are currently going on.");
			}
		}
		
		return true;
	}

}
