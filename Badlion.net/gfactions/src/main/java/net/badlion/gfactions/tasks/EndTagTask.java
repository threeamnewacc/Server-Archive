package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EndTagTask extends BukkitRunnable {

	private GFactions plugin;

	private Map<String, Location> map1;
	private HashSet<String> tagged;

	public EndTagTask(GFactions plugin) {
		this.plugin = plugin;

		this.map1 = new HashMap<String, Location>();
		this.tagged = new HashSet<String>();
	}

	@Override
	public void run() {
		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			if (this.plugin.isInCombat(player)) {
				// Already tagged
				if (this.tagged.contains(player.getUniqueId().toString())) {
					// They are now in end portal range, TP them back
					ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(player.getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
					if (region != null && region.getRegionName().equals("theendportal")) {
						Location location = this.map1.get(player.getUniqueId().toString());
						if (location != null) {
							player.teleport(location);
							player.sendMessage(ChatColor.RED + "Cannot enter end portal region when combat tagged.  Use /ct to see how much time you have left.");
						}
					} else {
						// Update their location
						this.map1.put(player.getUniqueId().toString(), player.getLocation());
					}
				} else {
					// Add them to the system
					this.tagged.add(player.getUniqueId().toString());
					this.map1.put(player.getUniqueId().toString(), player.getLocation());
				}
			} else if (this.tagged.contains(player.getUniqueId().toString())) {
				// They were tagged, now they aren't, remove them from our system
				this.tagged.remove(player.getUniqueId().toString());
				this.map1.remove(player.getUniqueId().toString());
			}
		}
	}

}
