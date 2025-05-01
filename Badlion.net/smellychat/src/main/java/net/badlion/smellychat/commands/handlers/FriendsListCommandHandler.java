package net.badlion.smellychat.commands.handlers;

import net.badlion.gberry.Gberry;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FriendsListCommandHandler {

	public static void printFriendsList(final Player player) {
		// Do everything async because we may need to grab usernames of offline players
		SmellyChat.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
			@Override
			public void run() {
				ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

				StringBuilder sb = new StringBuilder();
				for (UUID uuid : chatSettings.getFriendsList()) {
					Player pl = Bukkit.getPlayer(uuid);
					String name;
					if (pl != null) {
						name = ChatColor.GREEN + pl.getName();
					} else {
						name = ChatColor.RED + Gberry.getUsernameFromUUID(uuid);
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

				player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Friends [" + chatSettings.getFriendsList().size() + "] :");
				player.sendMessage(ChatColor.GOLD + "  - " + str);
			}
		});
	}

	public static void addToFriendsList(final Player player, final String[] args) {
		final Player friend = Bukkit.getPlayerExact(args[1]);
		if (friend != null) {
			// Check if player is a staff member
			if (friend.hasPermission("badlion.staff")) {
				player.sendMessage(ChatColor.RED + "You can not ignore staff members!");
				return;
			}

			FriendsListCommandHandler.addToFriendsListUUID(player, friend, args, friend.getUniqueId());
		} else {
			// Grab their uuid if they're offline
			Bukkit.getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
				@Override
				public void run() {
					final UUID uuid = Gberry.getOfflineUUID(args[1]);
					if (uuid != null) {
						// Check if player is a staff member
						if (SmellyChat.getInstance().getGPermissions().userHasPermission(uuid.toString(), "badlion.staff")) {
							player.sendMessage(ChatColor.RED + "You can not ignore mods, silly peasant!");
							return;
						}

						FriendsListCommandHandler.addToFriendsListUUID(player, null, args, uuid);
					} else {
						player.sendMessage(ChatColor.RED + "Invalid player name.");
					}
				}
			});
		}
	}

	private static void addToFriendsListUUID(Player player, Player ignore, String[] args, UUID uuid) {
		if (player == ignore) {
			player.sendMessage(ChatColor.RED + "You cannot add yourself to your friends list!");
			return;
		}

		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
		if (chatSettings.addToFriendsList(uuid)) {
			player.sendMessage(ChatColor.GREEN + "You have added " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " to your friends list.");
		} else {
			player.sendMessage(ChatColor.RED + args[1] + " is already on your friends list.");
		}
	}

	public static void removeFromFriendsList(final Player player, final String[] args) {
		final Player unfriend = Bukkit.getPlayerExact(args[1]);
		if (unfriend != null) {
			FriendsListCommandHandler.removeFromFriendsListUUID(player, args, unfriend.getUniqueId());
		} else {
			// Grab their uuid if they're offline
			Bukkit.getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
				@Override
				public void run() {
					final UUID uuid = Gberry.getOfflineUUID(args[1]);
					if (uuid != null) {
						FriendsListCommandHandler.removeFromFriendsListUUID(player, args, uuid);
					} else {
						player.sendMessage(ChatColor.RED + "Invalid player name.");
					}
				}
			});
		}
	}

	private static void removeFromFriendsListUUID(Player player, String[] args, UUID uuid) {
		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
		if (chatSettings.removeFromFriendsList(uuid)) {
			player.sendMessage(ChatColor.GREEN + "You have removed " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " from your friends list.");
		} else {
			player.sendMessage(ChatColor.RED + args[1] + " is not on your friends list.");
		}
	}

}
