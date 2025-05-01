package net.badlion.mpgchat;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MPGChannelHandler implements ChannelHandler {

    @Override
    public void sendMessageToChannel(Player player, String message, Channel channel) {
	    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

	    switch (channel.getIdentifier()) {
		    case "t":
		    case "T":
			    if (MPG.GAME_TYPE != MPG.GameType.PARTY) {
				    player.sendMessage(ChatColor.RED + "You are not in a team!");
				    return;
			    }

			    String fullMessage = mpgPlayer.getTeam().getColor() + "[Team]" + ChatColor.BLUE + player.getDisplayName() + channel.getColor() + ": " + message;

			    ChatMessage chatMessage = MPGChat.getInstance().getServer().createChatMessage(fullMessage, false);
			    for (UUID uuid : mpgPlayer.getTeam().getUUIDs()) {
				    // Send the message
				    chatMessage.sendTo(MPGChat.getInstance().getServer().getPlayer(uuid));
			    }
			    break;
		    default:
			    if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
				    player.sendMessage(ChatColor.RED + "Global chat is disabled");
				    return;
			    }

			    String mpgPrefix = "";

			    if (mpgPlayer.getState() == MPGPlayer.PlayerState.HOST) { // Player is host (Always talks in global)
				    mpgPrefix = ChatColor.DARK_RED + "[Host]";
			    } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.MOD) { // Mod
				    mpgPrefix = ChatColor.DARK_AQUA + "[UHC-Mod]";
			    } else if (MPG.GAME_TYPE == MPG.GameType.PARTY) { // Team game?
				    if (mpgPlayer.getState() != MPGPlayer.PlayerState.SPECTATOR) {
					    // Attach team prefix
					    mpgPrefix = mpgPlayer.getTeam().getPrefix();
				    }
			    }

			    String prefix = "";

			    // Show prefixes only if you are not a host/mod
			    if (mpgPlayer.getState().ordinal() < MPGPlayer.PlayerState.MOD.ordinal()) {
				    prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
				    prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");
			    }

			    // Staff members for this server always talk in global when spectating
			    boolean isSpectator = mpgPlayer.getState() == MPGPlayer.PlayerState.SPECTATOR
					    && (!player.hasPermission(SmellyChat.getInstance().getReportMessagePermission()) || player.isDisguised());

			    // Remove uhc prefix and attach spectator tag if this player is a spectator
			    if (isSpectator) {
				    mpgPrefix = ChatColor.AQUA + "[Spectator]";
			    }

			    String str1 = mpgPrefix + prefix + ChatColor.BLUE + player.getDisplayName();
			    String str2 = ": " + message;

			    ChatMessage defaultChatMessage = MPGChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
			    for (Player pl : MPGChat.getInstance().getServer().getOnlinePlayers()) {
				    ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(pl);

				    // Have they muted global chat?
				    if (!(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT)) {
					    continue;
				    }

				    // Is player ignoring the sender?
				    if (!chatSettings.isIgnoring(player)) {
					    // Don't send to them if they are still in the game
					    MPGPlayer recipientMPGPlayer = MPGPlayerManager.getMPGPlayer(pl.getUniqueId());

					    // Don't send message if this message is going to spectator chat and if this player isn't a spectator
					    if (isSpectator && recipientMPGPlayer.getState().ordinal() <= MPGPlayer.PlayerState.DEAD.ordinal()) {
						    continue;
					    }

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
			    break;
	    }

	    // Log
	    SmellyChat.getInstance().logMessage(channel, player, message);
    }

}
