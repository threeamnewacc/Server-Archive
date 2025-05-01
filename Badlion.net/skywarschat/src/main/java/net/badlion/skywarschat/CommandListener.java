package net.badlion.skywarschat;

import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void playerCommandEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        String command = args[0].toLowerCase();
        if (command.equals("/msg") || command.equals("/tell") || command.equals("/pm")
                || command.equals("/t") || command.equals("/w") || command.equals("/whisp")
                || command.equals("/whisper")) {
            if (args.length < 2) {
                event.getPlayer().sendMessage(ChatColor.RED + "/msg [name] [msg]");
            }

            if (MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId()).getState() == MPGPlayer.PlayerState.DEAD
		            || MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId()).getState() == MPGPlayer.PlayerState.SPECTATOR) {
                Player pl = Bukkit.getServer().getPlayer(args[1]);
                if (pl == null) {
                    return; // Let SmellyChat complain that they aren't online
                }

                if (MPGPlayerManager.getMPGPlayer(pl.getUniqueId()).getState() == MPGPlayer.PlayerState.PLAYER) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot message players who are alive!");
                }
            }
        } else if (command.equalsIgnoreCase("/r")) {
            ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
            if (chatSettings.getLastPMSource() != null) {
                MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(chatSettings.getLastPMSource());
                if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot message players who are alive!");
                }
            }
        }
    }

}
