package net.badlion.mpgchat.commands;

import com.google.common.base.Joiner;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpgchat.MPGChat;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
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
	    if (!(sender instanceof Player)) return true;

	    Player player = (Player) sender;

	    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);
	    if (mpgPlayer.getState() != MPGPlayer.PlayerState.MOD && mpgPlayer.getState() != MPGPlayer.PlayerState.HOST
			    && mpgPlayer.getState() != MPGPlayer.PlayerState.SPECTATOR) {
		    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
		    return true;
	    }

	    if (args.length == 0) return false;

	    // Get all the players who can see spectator chat
	    List<MPGPlayer> mpgPlayers = new ArrayList<>();
	    mpgPlayers.addAll(MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.SPECTATOR));
	    mpgPlayers.addAll(MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.MOD));
	    mpgPlayers.addAll(MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.HOST));

	    String sgPrefix = "";

	    if (mpgPlayer.getState() == MPGPlayer.PlayerState.HOST) { // Player is host (Always talks in global)
		    sgPrefix = ChatColor.DARK_RED + "[Host]";
	    } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.MOD) { // Mod
		    sgPrefix = ChatColor.DARK_AQUA + "[Mod]";
	    }

	    String prefix = mpgPlayer.getState().ordinal() >= MPGPlayer.PlayerState.MOD.ordinal() ? "" :
			    SmellyChat.getInstance().getGPermissions().getUserGroupMetaPrefix(player.getUniqueId()).replace("&", "§");
	    String str1 = sgPrefix + prefix + ChatColor.BLUE + player.getDisplayName();
	    String str2 = ": " + Joiner.on(" ").skipNulls().join(args);
	    str1 = ChatColor.AQUA + "[Spectator]" + str1;

	    String message = str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2;

	    ChatMessage defaultChatMessage = MPGChat.getInstance().getServer().createChatMessage(message, false);
	    for (MPGPlayer mpgPlayer2 : mpgPlayers) {
		    Player pl = mpgPlayer2.getPlayer();
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

				    // Send components if their global chat color is default or just send the regular message
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

        return true;
    }

}
