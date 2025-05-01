package net.badlion.gberry.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;

public class PlayerQuitListener implements Listener {
	
	private MineKart server;
	
	public PlayerQuitListener(MineKart server) {
		this.server = server;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player instanceof Player) {
			// Stupid DC, u lose
			if (this.server.getPlayerToRacer().containsKey(player)) {
				Racer racer = this.server.getPlayerToRacer().get(player);
				racer.remove();
			}
			
			// Removes them from unranked if they are waiting for a match
			if (this.server.getUnrankedPlayersWaitingForMatch().contains(player)){
				this.server.getUnrankedPlayersWaitingForMatch().remove(player);
			}
			
			// Removes them from in matchmaking
			if (this.server.getInMatchMaking().contains(player)){
				this.server.getInMatchMaking().remove(player);
			}
			
			// Remove from force starting an unranked match
			if (this.server.getPlayerToForceStartDelay().containsKey(player)) {
				BukkitTask task = this.server.getPlayerToForceStartDelay().get(player);
				task.cancel();
				this.server.getPlayerToForceStartDelay().remove(player);
			}
			
			// Remove them from hosting a match if they were and all the players too
			if (this.server.getPlayerInviteAcceptedList().containsKey(player)) {
				ArrayList<Player> players = this.server.getPlayerInviteAcceptedList().get(player);
				for (Player p : players) {
					p.sendMessage(ChatColor.BLUE + "Host quit before starting match.  Taken out of matchmaking.");
				}
				this.server.getPlayerInviteAcceptedList().remove(player);
				this.server.getHostToRaceTask().remove(player);
			}
			
			// They had accepted a match and then dc before it starts
			if (this.server.getGuestToHostAccepted().containsKey(player)) {
				Player host = this.server.getGuestToHostAccepted().get(player);
				host.sendMessage(ChatColor.BLUE + player.getName() + " has left your game.");
				this.server.getPlayerInviteAcceptedList().get(host).remove(player);
				this.server.getGuestToHostAccepted().remove(player);
			}
			
			// Eject them if they were on a horse
			Horse horse = (Horse) player.getVehicle();
			if (horse != null) {
				horse.eject();
				horse.remove();
			}
		}
	}
}
