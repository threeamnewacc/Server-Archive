package net.badlion.uhcchat;

import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import net.badlion.uhc.listeners.gamemodes.OpsVsWorldGameMode;
import net.badlion.uhc.listeners.gamemodes.QuadrantsGameMode;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UHCChannelHandler implements ChannelHandler {

	private Set<UUID> playersWhoHaveSaidGG = new HashSet<>();

	@Override
	public void sendMessageToChannel(Player player, String message, Channel channel) {
		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

		switch (channel.getIdentifier()) {
			case "t":
			case "T":
				if (BadlionUHC.getInstance().getGameType() != UHCTeam.GameType.TEAM) {
					player.sendMessage(ChatColor.RED + "You are not in a team!");
					return;
				}

				String fullMessage = ChatColor.AQUA + "[Team]" + ChatColor.BLUE + player.getDisplayName() + channel.getColor() + ": " + message;

				ChatMessage chatMessage = UHCChat.getInstance().getServer().createChatMessage(fullMessage, false);
				for (UUID uuid : uhcPlayer.getTeam().getUuids()) {
					// Send the message
					chatMessage.sendTo(UHCChat.getInstance().getServer().getPlayer(uuid));
				}
				break;
			default:
				if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
					player.sendMessage(ChatColor.RED + "Global chat is disabled");
					return;
				}

				String uhcPrefix = "";

				boolean wasJustPlaying = uhcPlayer.getDeathTime() != null && (uhcPlayer.getDeathTime() + 30 * 1000) > System.currentTimeMillis();
				boolean isSpectator = uhcPlayer.getState() == UHCPlayer.State.SPEC && !wasJustPlaying;

				// Good sportsmanship bitches
				if (wasJustPlaying) {
					if (!message.equalsIgnoreCase("gg") && !message.equalsIgnoreCase("gf")
							&& !message.equalsIgnoreCase("thanks for hosting") && !message.equalsIgnoreCase("ty for hosting")
							&& !message.equalsIgnoreCase("thank you for hosting")&& !message.equalsIgnoreCase("thanks for host")
							&& !message.equalsIgnoreCase("thank you for host") && !message.equalsIgnoreCase("thx for hosting")
							&& !message.equalsIgnoreCase("thx for host") && !message.equalsIgnoreCase("ty for host")) {
						player.sendMessage(ChatColor.RED + "Thank the host, or say 'gg' or 'gf' and be a good sport.");
						return;
					}

					// Only allow it to be said once
					if (this.playersWhoHaveSaidGG.contains(player.getUniqueId())) {
						isSpectator = true;
					} else {
						this.playersWhoHaveSaidGG.add(player.getUniqueId());
					}
				}

				if (uhcPlayer.getState() == UHCPlayer.State.HOST) { // Player is host (Always talks in global)
					uhcPrefix = ChatColor.DARK_RED + "[Host]";
				} else if (uhcPlayer.getState() == UHCPlayer.State.MOD) { // Mod
					uhcPrefix = ChatColor.DARK_AQUA + "[UHC-Mod]";
				} else if (GameModeHandler.GAME_MODES.contains("QUADRANTS")) {
					uhcPrefix = QuadrantsGameMode.getPrefix(uhcPlayer);
				} else if (GameModeHandler.GAME_MODES.contains("OPS_VS_WORLD")) {
					uhcPrefix = OpsVsWorldGameMode.getPrefix(uhcPlayer);
				} else if (!BadlionUHC.getInstance().getGameType().equals(UHCTeam.GameType.SOLO)) { // There are teams
					if (uhcPlayer.isSolo() || (uhcPlayer.getTeam() != null && uhcPlayer.getTeam().getSize() > 1)) {
						// Team Chat
						uhcPrefix = uhcPlayer.getTeam().getPrefix();
					} // else Global
				} // else Global

				if (isSpectator) {
					uhcPrefix = "";
				}

				String prefix = "";

				// Show prefixes only if you are not a host/mod
				if (uhcPlayer.getState().ordinal() < UHCPlayer.State.MOD.ordinal()) {
					prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
					prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");
				}

				String str1 = uhcPrefix + prefix + ChatColor.BLUE + player.getDisplayName();
				String str2 = ": " + message;

				if (isSpectator) {
					str1 = ChatColor.AQUA + "[Spectator]" + str1;
				}

				ChatMessage defaultChatMessage = UHCChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
				for (Player pl : UHCChat.getInstance().getServer().getOnlinePlayers()) {
					ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(pl);

					// Have they muted global chat?
					if (!(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT)) {
						continue;
					}

					// Is player ignoring the sender?
					if (!chatSettings.isIgnoring(player)) {
						// Don't send to them if they are still in the game
						UHCPlayer uhcP = UHCPlayerManager.getUHCPlayer(pl.getUniqueId());

						// Debug
						if (uhcP == null) {
							try {
								throw new RuntimeException("UHCP NULL FOR " + pl.getName());
							} catch (RuntimeException e) {
								continue;
							}
						}

						if (isSpectator && uhcP.getState().ordinal() <= UHCPlayer.State.DEAD.ordinal()) {
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
