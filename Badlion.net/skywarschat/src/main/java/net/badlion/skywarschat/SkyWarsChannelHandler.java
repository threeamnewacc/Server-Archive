package net.badlion.skywarschat;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

public class SkyWarsChannelHandler implements ChannelHandler {

    @Override
    public void sendMessageToChannel(Player player, String message, Channel channel) {
	    if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
		    player.sendMessage(ChatColor.RED + "Global chat is disabled");
		    return;
	    }

	    String prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
	    prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");

	    String str1 = prefix + ChatColor.BLUE + player.getDisplayName();
	    String str2 = ": " + message;

	    // Spectator check
	    boolean isSpectator = MPG.getInstance().getServerState().ordinal() >= MPG.ServerState.GAME.ordinal()
			    && MPG.getInstance().getMPGGame().getGameState().ordinal() < MPGGame.GameState.POST_GAME.ordinal()
			    && (MPGPlayerManager.getMPGPlayer(player.getUniqueId()).getState() == MPGPlayer.PlayerState.DEAD
			    || MPGPlayerManager.getMPGPlayer(player.getUniqueId()).getState() == MPGPlayer.PlayerState.SPECTATOR);

	    if (isSpectator) {
		    str1 = ChatColor.AQUA + "[Spectator]" + str1;
	    }

	    // Log the message
	    SmellyChat.getInstance().logMessage(channel, player, message);

	    ChatMessage defaultChatMessage = SkyWarsChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
	    for (Player pl : SmellyChat.getInstance().getServer().getOnlinePlayers()) {
		    ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(pl);

		    // Have they muted global chat?
		    if (!(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT)) {
			    continue;
		    }

		    // Is player ignoring the sender?
		    if (!chatSettings.isIgnoring(player)) {
			    // Marked player check
			    ChatColor markedPlayerColor = chatSettings.getMarkedPlayerColor(player);
			    if (markedPlayerColor != null) {
				    pl.sendMessage(str1 + markedPlayerColor + str2);
				    continue;
			    }

			    // MPGPlayer check
			    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(pl.getUniqueId());
			    if (isSpectator && mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER && !pl.hasPermission("badlion.sgtrial")) {
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

}
