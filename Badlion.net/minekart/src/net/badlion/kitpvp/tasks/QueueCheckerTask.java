package net.badlion.kitpvp.tasks;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.tinywebteam.badlion.MineKart;

public class QueueCheckerTask extends BukkitRunnable {
	
	private MineKart plugin;
	private Player player;
	
	public QueueCheckerTask(MineKart plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	@Override
	public void run() {
		boolean foundTrack = false;
		ArrayList<Integer> randomTracksToChooseFrom = new ArrayList<Integer>();
		for (int i = 0; i < this.plugin.getAvailableTracks().size(); ++i) {
			if (this.plugin.getAvailableTracks().get(i) == true) {
				foundTrack = true;
				randomTracksToChooseFrom.add(i);
			}
		}
		if (!foundTrack) {
			return; // wait a bit longer
		}
		
		// Force run
		ArrayList<Player> players = new ArrayList<Player>();
		int initialSize = this.plugin.getUnrankedPlayersWaitingForMatch().size();
		if (initialSize < 2) {
			return; // not enuff players yet
		}
		for (int i = 0; i < initialSize; ++i) {
			Player player = this.plugin.getUnrankedPlayersWaitingForMatch().remove();
			players.add(player);
			
			// Remove from force task stuff
			if (player.equals(this.player)) {
				this.plugin.getPlayerToForceStartDelay().remove(this.player);
			} else {
				BukkitTask task = this.plugin.getPlayerToForceStartDelay().get(player);
				task.cancel();
				this.plugin.getPlayerToForceStartDelay().remove(player);
			}
			
			if (this.plugin.getPremiumQueueNames().contains(players.get(i).getName())) {
				this.plugin.setPremiumMembersInQueue(this.plugin.getPremiumMembersInQueue() - 1);
			}
		}

		try {
			this.plugin.createMatch(players, 0, randomTracksToChooseFrom);
			
			//player1.sendMessage(ChatColor.BLUE + "Now in unranked match with " + player2.getName());
			//player2.sendMessage(ChatColor.BLUE + "Now in unranked match with " + player1.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Bukkit.getLogger().severe("Out of tracks.");
			for (int i = 0; i < players.size(); ++i) {
				this.plugin.removePlayerFromMatchmaking(players.get(i));
				players.get(i).sendMessage(ChatColor.RED + "No tracks available.  Try again later.");
			}
			e.printStackTrace();
		}
	}
	

}
