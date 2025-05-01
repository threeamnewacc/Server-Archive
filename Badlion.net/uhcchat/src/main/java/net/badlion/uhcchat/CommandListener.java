package net.badlion.uhcchat;

import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static net.badlion.uhc.UHCPlayer.State.PLAYER;

public class CommandListener implements Listener {

    @EventHandler
    public void onPLayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        String command = args[0].toLowerCase();
        if (command.equals("/msg") || command.equals("/tell") || command.equals("/pm")
                || command.equals("/t") || command.equals("/w") || command.equals("/whisp")
                || command.equals("/whisper") || command.equals("message")) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
            if (uhcPlayer.getState() == UHCPlayer.State.SPEC || uhcPlayer.getState() == UHCPlayer.State.SPEC_IN_GAME) {
                Player pl = UHCChat.getInstance().getServer().getPlayerExact(args[1]);
                if (pl != null) {
                    UHCPlayer uhcPlayer2 = UHCPlayerManager.getUHCPlayer(pl.getUniqueId());
                    if (uhcPlayer2.getState() == PLAYER) {
                        player.sendMessage(ChatColor.RED + "You cannot PM players who are still alive!");
                        event.setCancelled(true);
                    }
                }
            } else if (uhcPlayer.getState() == PLAYER) {
	            Player pl = UHCChat.getInstance().getServer().getPlayerExact(args[1]);
	            if (pl != null) {
		            UHCPlayer uhcPlayer2 = UHCPlayerManager.getUHCPlayer(pl.getUniqueId());
		            if (uhcPlayer2.getState() == UHCPlayer.State.SPEC || uhcPlayer2.getState() == UHCPlayer.State.SPEC_IN_GAME) {
			            player.sendMessage(ChatColor.RED + "You cannot PM players who aren't playing!");
			            event.setCancelled(true);
		            }
	            }
            }
        } else if (command.equalsIgnoreCase("/r")) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
            ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
	        if (chatSettings.getLastPMSource() != null) {
		        if (uhcPlayer.getState() == UHCPlayer.State.SPEC || uhcPlayer.getState() == UHCPlayer.State.SPEC_IN_GAME) {
			        UHCPlayer uhcPlayer2 = UHCPlayerManager.getUHCPlayer(chatSettings.getLastPMSource());
			        if (uhcPlayer2.getState() == PLAYER) {
				        player.sendMessage(ChatColor.RED + "You cannot PM players who are still alive!");
				        event.setCancelled(true);
			        }
		        } else if (uhcPlayer.getState() == PLAYER) {
			        UHCPlayer uhcPlayer2 = UHCPlayerManager.getUHCPlayer(chatSettings.getLastPMSource());
			        if (uhcPlayer2.getState() == UHCPlayer.State.SPEC || uhcPlayer2.getState() == UHCPlayer.State.SPEC_IN_GAME) {
				        player.sendMessage(ChatColor.RED + "You cannot PM players who aren't playing!");
				        event.setCancelled(true);
			        }
		        }
	        }
        } else if (event.getMessage().equalsIgnoreCase("/ch t")) {
            if (BadlionUHC.getInstance().getGameType() != UHCTeam.GameType.TEAM) {
                player.sendMessage(ChatColor.RED + "You are not in a team!");
                event.setCancelled(true);;
            }
        }
    }


}
