package net.badlion.kitpvp.tasks;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.MineKart;

public class RaceStartDelayTask extends BukkitRunnable {
	
	private MineKart plugin;
	private Player host;
	
	public RaceStartDelayTask(MineKart plugin, Player player) {
		this.plugin = plugin;
		this.host = player;
	}
	
	@Override
	public void run () {
		// Force start race, its been 2 minutes
		ArrayList<Player> players = this.plugin.getPlayerInviteAcceptedList().get(this.host);
		if (players != null && players.size() > 1) {
			this.host.performCommand("start");
		} else {
			// Ok, no one loves you, just go home
			this.plugin.getPlayerInviteAcceptedList().remove(this.host);
			this.plugin.getNumOfInvitesSent().remove(this.host);
			this.plugin.getInMatchMaking().remove(this.host);
			this.host.sendMessage(ChatColor.RED + "Not enough players to begin race.");
		}
	}
}
