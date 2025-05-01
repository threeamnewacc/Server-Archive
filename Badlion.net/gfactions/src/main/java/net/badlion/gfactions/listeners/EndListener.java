package net.badlion.gfactions.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPostPortalEvent;

public class EndListener implements Listener {

	@EventHandler(priority=EventPriority.LAST)
	public void onPlayerTeleportToEndViaPortal(final PlayerPostPortalEvent event) {
		if (event.getFrom() != null && event.getTo() != null) {
			if (event.getTo().getWorld().getName().equals("world_the_end")) {
				event.setTo(this.getRandomEnterLocation());

				// Remove pvp protection if they have it
				if (GFactions.plugin.getMapNameToPvPTimeRemaining().containsKey(event.getPlayer().getUniqueId().toString())) {
					// Their PVP protection is over, time to remove from the system
					GFactions.plugin.getMapNameToPvPTimeRemaining().remove(event.getPlayer().getUniqueId().toString());
					GFactions.plugin.getMapNameToJoinTime().remove(event.getPlayer().getUniqueId().toString());

					GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {

						@Override
						public void run() {
							// Purge from DB
							GFactions.plugin.removeProtection(event.getPlayer());
						}
					});

					event.getPlayer().sendMessage(ChatColor.RED + "Entered the End, lost PVP Protection.");
				}
			} else if (event.getFrom().getWorld().getName().equals("world_the_end")) {
				event.setTo(this.getRandomExitLocation());
			}
		}
	}

	private Location getRandomEnterLocation() {
		boolean flag = false;
		Location location = GFactions.plugin.getEndEnterLocations().get(Gberry.generateRandomInt(0, GFactions.plugin.getEndEnterLocations().size() - 1)).clone();

		while (!flag || location.getBlock().getType() != Material.AIR) {
			flag = true;
			location.add(Gberry.generateRandomInt(0, 3), 0, Gberry.generateRandomInt(0, 3));
		}

		return location;
	}

	private Location getRandomExitLocation() {
		boolean flag = false;
		Location location = GFactions.plugin.getEndExitLocation().clone();

		while (!flag || location.getBlock().getType() != Material.AIR) {
			flag = true;
			location.add(Gberry.generateRandomInt(0, 12), 0, Gberry.generateRandomInt(0, 12));
		}

		return location;
	}

}
