package net.badlion.smellychat.commands.handlers;

import net.badlion.gberry.Gberry;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IgnoreListCommandHandler {

	public static void printIgnoreList(final Player player) {
		// Do everything async because we may need to grab usernames of offline players
		SmellyChat.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
			@Override
			public void run() {
				ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

				StringBuilder sb = new StringBuilder();
				for (UUID uuid : chatSettings.getIgnoredList()) {
					Player pl = Bukkit.getPlayer(uuid);
					String name;
					if (pl != null) {
						name = pl.getName();
					} else {
						name = Gberry.getUsernameFromUUID(uuid);
					}

					sb.append(name);
					sb.append(", ");
				}

				String str = sb.toString();
				if (!str.isEmpty()) {
					str = str.substring(0, str.length() - 2);
				} else {
					str = "None";
				}

				player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Ignored Players [" + chatSettings.getIgnoredList().size() + "] :");
				player.sendMessage(ChatColor.GOLD + "  - " + str);
			}
		});
	}

	public static void addToIgnoreList(final Player player, final String[] args) {
		final Player ignore = Bukkit.getPlayerExact(args[1]);
		if (ignore != null) {
			// Check if player is a staff member
			if (ignore.hasPermission("badlion.staff")) {
				player.sendMessage(ChatColor.RED + "You can not ignore staff members!");
				return;
			}

			IgnoreListCommandHandler.addToIgnoreListUUID(player, ignore, args, ignore.getUniqueId());
		} else {
			// Grab their uuid if they're offline
			Bukkit.getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
				@Override
				public void run() {
					final UUID uuid = Gberry.getOfflineUUID(args[1]);
					if (uuid != null) {
						// Check if player is a staff member
						if (SmellyChat.getInstance().getGPermissions().userHasPermission(uuid.toString(), "badlion.staff")) {
							player.sendMessage(ChatColor.RED + "You can not ignore staff members!");
							return;
						}

						IgnoreListCommandHandler.addToIgnoreListUUID(player, null, args, uuid);
					} else {
						player.sendMessage(ChatColor.RED + "Invalid player name.");
					}
				}
			});
		}
	}

	private static void addToIgnoreListUUID(Player player, Player ignore, String[] args, UUID uuid) {
		if (player == ignore) {
			player.sendMessage(ChatColor.RED + "You cannot ignore yourself!");
			return;
		}

		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
		if (chatSettings.addToIgnoredList(uuid)) {
			player.sendMessage(ChatColor.GREEN + "You have added " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " to your ignore list.");
		} else {
			player.sendMessage(ChatColor.RED + args[1] + " is already on your ignore list.");
		}
	}

	public static void removeFromIgnoreList(final Player player, final String[] args) {
		final Player unignore = Bukkit.getPlayerExact(args[1]);
		if (unignore != null) {
			IgnoreListCommandHandler.removeFromIgnoreListUUID(player, args, unignore.getUniqueId());
		} else {
			// Grab their uuid if they're offline
			Bukkit.getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
				@Override
				public void run() {
					final UUID uuid = Gberry.getOfflineUUID(args[1]);
					if (uuid != null) {
						IgnoreListCommandHandler.removeFromIgnoreListUUID(player, args, uuid);
					} else {
						player.sendMessage(ChatColor.RED + "Invalid player name.");
					}
				}
			});
		}
	}

	private static void removeFromIgnoreListUUID(Player player, String[] args, UUID uuid) {
		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
		if (chatSettings.removeFromIgnoredList(uuid)) {
			player.sendMessage(ChatColor.GREEN + "You have removed " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " from your ignore list.");
		} else {
			player.sendMessage(ChatColor.RED + args[1] + " is not on your ignore list.");
		}
	}

}
