package net.badlion.kitpvp.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.MineKart;

public class InviteDelayTask extends BukkitRunnable {
	
	private MineKart plugin;
	private Player guest;
	
	public InviteDelayTask(MineKart plugin, Player player) {
		this.plugin = plugin;
		this.guest = player;
	}
	
	@Override
	public void run () {
		this.plugin.getGuestToInviteeMap().remove(this.guest);
		this.plugin.getGuestToTask().remove(this.guest);
		this.plugin.getInMatchMaking().remove(this.guest);
		this.guest.sendMessage(ChatColor.BLUE + "Invite to race has expired.");
	}
}
