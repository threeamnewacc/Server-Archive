package net.badlion.uhcchat;

import com.google.common.base.Joiner;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectatorChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
            if (uhcPlayer.getState() != UHCPlayer.State.MOD && uhcPlayer.getState() != UHCPlayer.State.HOST) {
                player.sendMessage("You do not have permission to use this command");
                return true;
            }

	        if (args.length == 0) {
		        return false;
	        }

	        // Get all the players who can see spectator chat
            List<UHCPlayer> uhcPlayers = new ArrayList<>();
            uhcPlayers.addAll(UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC));
            uhcPlayers.addAll(UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD));
            uhcPlayers.addAll(UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.HOST));

            String uhcPrefix = "";

            if (uhcPlayer.getState() == UHCPlayer.State.HOST) {
                uhcPrefix = ChatColor.DARK_RED + "[Host]";
            } else if (uhcPlayer.getState() == UHCPlayer.State.MOD) { // Mod
                uhcPrefix = ChatColor.DARK_AQUA + "[UHC-Mod]";
            }

            String prefix = uhcPlayer.getState().ordinal() >= UHCPlayer.State.MOD.ordinal() ? "" :
                                    SmellyChat.getInstance().getGPermissions().getUserGroupMetaPrefix(player.getUniqueId()).replace("&", "§");
            String str1 = uhcPrefix + prefix + ChatColor.BLUE + player.getDisplayName();
            String str2 = ": " + Joiner.on(" ").skipNulls().join(args);
            str1 = ChatColor.AQUA + "[Spectator]" + str1;

	        String message = str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2;

	        ChatMessage defaultChatMessage = UHCChat.getInstance().getServer().createChatMessage(message, false);
	        for (UHCPlayer uhcp : uhcPlayers) {
                Player pl = Bukkit.getPlayer(uhcp.getUUID());
                if (pl != null) {
                    ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(pl);
                    // Is player ignoring the sender?
                    if (!chatSettings.isIgnoring(player)) {
                        // Marked player check
                        ChatColor markedPlayerColor = chatSettings.getMarkedPlayerColor(player);
                        if (markedPlayerColor != null) {
                            pl.sendMessage(str1 + markedPlayerColor + str2);
                            continue;
                        }

	                    // Send components if their global chat color is default
	                    ChatColor globalChatColor = (ChatColor) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR);
	                    if (globalChatColor == ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue()) {
		                    // Send the message
		                    defaultChatMessage.sendTo(pl);
	                    } else {
		                    // They use a different color for global chat, send them a different chat message
		                    SmellyChat.getInstance().getServer().createChatMessage(str1 + globalChatColor + str2, false).sendTo(pl);
	                    }
                    }
                }
            }

	        // Log (
	        SmellyChat.getInstance().logMessage("G", player, message);
        }

        return true;
    }

}
