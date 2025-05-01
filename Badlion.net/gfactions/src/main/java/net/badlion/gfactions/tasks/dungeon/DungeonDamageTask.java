package net.badlion.gfactions.tasks.dungeon;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.badlion.gfactions.managers.DungeonManager;
import net.badlion.gfactions.GFactions;

public class DungeonDamageTask extends BukkitRunnable {
	
	private GFactions plugin;
	private DungeonManager dm;
	
	public DungeonDamageTask(GFactions plugin) {
		this.plugin = plugin;
		this.dm = this.plugin.getDungeonManager();
	}
	
	@Override
	public void run() {
		List<Player> players = this.dm.getWorld().getPlayers();
		
		// Damage the little bitches trying to cheat
		for (Player player : players) {
			if (player.getLocation().getBlockY() > this.dm.getCurrentDungeon().getMaxPlayerLevelY()) {
				player.setHealth(player.getHealth() > 5 ? player.getHealth() - 5 : 0); // 5 hearts u slimy bitch
				player.sendMessage(ChatColor.RED + "No trying to cheat the domage. POW");
			}
		}
	}

}
