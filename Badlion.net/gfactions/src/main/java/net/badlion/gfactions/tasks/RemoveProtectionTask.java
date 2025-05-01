package net.badlion.gfactions.tasks;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.GFactions;

public class RemoveProtectionTask extends BukkitRunnable {
	
	private GFactions plugin;
	
	public RemoveProtectionTask(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		List<Player> players = plugin.getServer().getWorld("world").getPlayers();
		for (final Player player : players) {
			// Are they still protected
			Integer time = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
			if (time != null) {
				Faction faction = Board.getFactionAt(player.getLocation());
				try {
					int id = Integer.parseInt(faction.getId());
					// On enemy land or their own
					if (id > 0) {
						this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());
						this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
						
						this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
							
							@Override
							public void run() {
								// Purge from DB
								plugin.removeProtection(player);
							}
						});
						player.sendMessage(ChatColor.RED + "You have entered a faction's land.  Your PVP Protection has been removed.");
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

}
