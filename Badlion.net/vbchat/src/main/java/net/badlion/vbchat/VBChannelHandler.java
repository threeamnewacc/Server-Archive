package net.badlion.vbchat;

import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.PlayerData;
import net.kohi.vaultbattle.type.Team;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

public class VBChannelHandler implements ChannelHandler {

    @Override
    public void sendMessageToChannel(Player player, String message, Channel channel) {

        PlayerData playerData = VaultBattlePlugin.getPlugin().getPlayerDataManager().get(player);
        Team team = playerData.getTeam();
        ChatColor teamColor = team != null ? team.getColor().toChatColor() : null;
        String prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
        prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");

        switch (channel.getIdentifier()) {
            case "T":
            case "t":
                if (team == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a team! Returning you to global chat.");
                    ChatSettingsManager.getChatSettings(player).setActiveChannel("G");
                    return;
                }

                String fullMessage = teamColor + "[Team] " + prefix + teamColor + player.getName() + ChatColor.WHITE + ": " + message;
                ChatMessage chatMessage = VBChat.getInstance().getServer().createChatMessage(fullMessage, false);
                for (Player pl : team.getOnlinePlayers()) {
                    chatMessage.sendTo(pl);
                }
                break;
            default:
                if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
                    player.sendMessage(ChatColor.RED + "Global chat is disabled");
                    return;
                }

                String str1;
                String str2 = ": " + message;

                if (playerData.isSpectating()) {
                    str1 = ChatColor.GRAY + "(Spectator) " + ChatColor.stripColor(prefix) + player.getName();
                } else if (team != null) {
                    str1 = ChatColor.GRAY + "(" + team.getName() + ChatColor.GRAY + ") " + prefix + teamColor + player.getName();
                } else {
                    str1 = prefix + ChatColor.BLUE + player.getDisplayName();
                }

                ChatMessage defaultChatMessage = VBChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
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
