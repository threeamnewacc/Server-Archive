package net.badlion.smellychat.commands;

import net.badlion.gberry.Gberry;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MarkCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			if (args.length == 2) {
				Player marked = Bukkit.getPlayerExact(args[0]);

				// Note: We no longer impose this restriction
				/*if (player == marked) {
					player.sendMessage(ChatColor.RED + "You can not mark your own chat!");
					return true;
				}*/

				if (marked != null) {
					ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

					if (args[1].equalsIgnoreCase("remove")) {
						if (chatSettings.removeMarkedPlayer(marked.getUniqueId())) {
							player.sendMessage(ChatColor.GREEN + "Unmarked " + args[0] + ".");
						} else {
							player.sendMessage(ChatColor.RED + args[0] + "'s chat is not marked.");
						}
						return true;
					}

					try {
						// Get chat color
						ChatColor color = ChatColor.valueOf(args[1].toUpperCase());

						// Mark player
						chatSettings.addMarkedPlayer(marked.getUniqueId(), color);

						player.sendMessage(ChatColor.GREEN + "Marked " + marked.getName() + " as " + color + args[1] + ChatColor.GREEN + ".");
					} catch (IllegalArgumentException e) {
						player.sendMessage(ChatColor.RED + "Chat color not found.");
					}
				} else {
					// Grab their uuid if they're offline
					Bukkit.getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
						@Override
						public void run() {
							final UUID uuid = Gberry.getOfflineUUID(args[0]);
							ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
							if (uuid != null) {
								if (args[1].equalsIgnoreCase("remove")) {
									if (chatSettings.removeMarkedPlayer(uuid)) {
										player.sendMessage(ChatColor.GREEN + "Unmarked " + args[0] + ".");
									} else {
										player.sendMessage(ChatColor.RED + args[0] + "'s chat is not marked.");
									}
									return;
								}

								try {
									// Get chat color
									ChatColor color = ChatColor.valueOf(args[1].toUpperCase());

									// Mark player
									chatSettings.addMarkedPlayer(uuid, color);

									player.sendMessage(ChatColor.GREEN + "Marked " + args[0] + " as " + color + args[1] + ChatColor.GREEN + ".");
								} catch (IllegalArgumentException e) {
									player.sendMessage(ChatColor.RED + "Chat color not found.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "Invalid player name.");
							}
						}
					});
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					final ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
					if (!chatSettings.getMarkedPlayers().isEmpty()) {
						SmellyChat.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
							@Override
							public void run() {
								StringBuilder sb = new StringBuilder();
								for (UUID uuid : chatSettings.getMarkedPlayers().keySet()) {
									sb.append(chatSettings.getMarkedPlayerColor(uuid));
									sb.append(Gberry.getUsernameFromUUID(uuid));
									sb.append(ChatColor.WHITE);
									sb.append(", ");
								}

								String str = sb.toString();
								str = str.substring(0, str.length() - 2);

								player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Marked Players [" + chatSettings.getMarkedPlayers().size() + "] :");
								player.sendMessage(ChatColor.GOLD + "  - " + str);
							}
						});
					} else {
						player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Marked Players [" + chatSettings.getMarkedPlayers().size() + "] :");
						player.sendMessage(ChatColor.GOLD + "  - None");
					}
				} else if (s.equalsIgnoreCase("unmark")) {
					player.performCommand("mark " + args[0] + " remove");
				} else {
					this.helpMessage(player);
				}
			} else {
				this.helpMessage(player);
			}
		}
		return true;
	}

	private void helpMessage(Player player) {
		player.sendMessage(ChatColor.AQUA + "===Mark Commands===");
		player.sendMessage(ChatColor.GOLD + "/mark list - List all marked players");
		player.sendMessage(ChatColor.GOLD + "/mark <player> <color> - Mark a player's chat");
		player.sendMessage(ChatColor.GOLD + "/unmark <player> - Unmark a player's chat");
	}

}
