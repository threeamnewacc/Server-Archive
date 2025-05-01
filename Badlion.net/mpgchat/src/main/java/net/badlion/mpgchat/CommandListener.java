package net.badlion.mpgchat;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

	@EventHandler
	public void onPLayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] args = event.getMessage().split(" ");
		String command = args[0].toLowerCase();
		if (command.equals("/msg") || command.equals("/tell") || command.equals("/pm")
				|| command.equals("/t") || command.equals("/w") || command.equals("/whisp")
				|| command.equals("/whisper")) {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);
			if (mpgPlayer.getState() == MPGPlayer.PlayerState.SPECTATOR) {
				Player pl = MPGChat.getInstance().getServer().getPlayerExact(args[1]);
				if (pl != null) {
					MPGPlayer mpgPlayer2 = MPGPlayerManager.getMPGPlayer(pl);
					if (mpgPlayer2.getState() == MPGPlayer.PlayerState.PLAYER) {
						player.sendMessage(ChatColor.RED + "You cannot PM players who are still alive!");
						event.setCancelled(true);
					}
				}
			}
		} else if (command.equalsIgnoreCase("/r")) {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);
			ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
			if (chatSettings.getLastPMSource() != null
					&& mpgPlayer.getState() == MPGPlayer.PlayerState.SPECTATOR) {
				MPGPlayer mpgPlayer2 = MPGPlayerManager.getMPGPlayer(chatSettings.getLastPMSource());
				if (mpgPlayer2.getState() == MPGPlayer.PlayerState.PLAYER) {
					player.sendMessage(ChatColor.RED + "You cannot PM players who are still alive!");
					event.setCancelled(true);
				}
			}
		} else if (event.getMessage().equalsIgnoreCase("/ch t")) {
			if (MPG.GAME_TYPE != MPG.GameType.PARTY) {
				player.sendMessage(ChatColor.RED + "You are not in a team!");
				event.setCancelled(true);
			}
		}
	}

}
