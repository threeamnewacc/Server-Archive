package net.badlion.arenapvpchat;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

public class ArenaPvPChannelHandler implements ChannelHandler {

    @Override
    public void sendMessageToChannel(Player player, String message, Channel channel) {
	    Team team = ArenaPvP.getInstance().getPlayerTeam(player);
	    Match match = MatchManager.getActiveMatches().get(team);

	    // Match chat
	    if (match != null) {
		    String fullMessage = ChatColor.GOLD + "[Match]" + ChatColor.GRAY + player.getDisplayName() + ChatColor.WHITE + ": " + message;

		    ChatMessage chatMessage = ArenaPvPChat.getInstance().getServer().createChatMessage(fullMessage, false);
		    for (Player pl : match.getPlayers()) {
			    // Send the message
			    chatMessage.sendTo(pl);
		    }

		    // TODO: SEND TO THIS MATCH'S SPECTATORS TOO
		    // TODO: SPECTATOR CHAT JUST FOR THIS MATCH'S SPECTATORS?
	    } else { // Spectator chat
		    String prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
		    prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");
		    String str1 = prefix + ChatColor.BLUE + player.getDisplayName();
		    String str2 = ": " + message;

		    // Add spectator tag
		    str1 = ChatColor.AQUA + "[Spectator]" + str1;

		    ChatMessage defaultChatMessage = ArenaPvPChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
		    for (State<Player> state : TeamStateMachine.getInstance().getStates()) {
			    if (state == TeamStateMachine.spectatorState || state == TeamStateMachine.followState) {
				    for (Player pl : state.getElements()) {
					    ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(pl);

					    // Is player ignoring the sender?
					    if (!chatSettings.isIgnoring(player)) {
						    // Marked player check
						    ChatColor markedPlayerColor = chatSettings.getMarkedPlayerColor(player);
						    if (markedPlayerColor != null) {
							    pl.sendMessage(str1 + markedPlayerColor + str2);
							    continue;
						    }

						    // Send the message, no custom global chat colors here
						    defaultChatMessage.sendTo(pl);
					    }
				    }
			    }
		    }
	    }

	    // Log
	    SmellyChat.getInstance().logMessage(channel, player, message);
    }

}
