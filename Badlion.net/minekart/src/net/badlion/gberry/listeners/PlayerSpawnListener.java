package net.badlion.gberry.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.tinywebteam.badlion.MineKart;

public class PlayerSpawnListener implements Listener {
	
	private MineKart server;
	private String [] welcomeMessage;
	
	public PlayerSpawnListener(MineKart server) {
		this.server = server;
		
		StringBuilder string = new StringBuilder();
		string.append(ChatColor.BLUE);
		string.append("Welcome to Badlion Minekart.\n");
		string.append(ChatColor.GOLD);
		string.append("Read the signs around you or use /race to join matchmaking.\n");
		string.append(ChatColor.GOLD);
		string.append("Help support the server by becoming a donator and get extra cool benefits.\n");
		string.append(ChatColor.GOLD);
		string.append("www.badlion.net\n");
		string.append(ChatColor.BLUE);
		string.append("=====================================================");
		this.welcomeMessage = string.toString().split("\n");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player instanceof Player) {
			this.server.tpPlayerToSpawn(player);
			if (!player.hasPlayedBefore()) {
				player.sendMessage(this.welcomeMessage);
			} else {
				player.sendMessage(ChatColor.BLUE + "Welcome to Badlion Minekart.");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		event.setRespawnLocation(new Location(player.getWorld(), 0.5, 71, 0.5)); // HARDCODED
	}

}
