package net.badlion.gpermissions;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gpermissions.bukkitevents.GroupChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserCommand implements CommandExecutor {

	private GPermissions plugin;

	public UserCommand(GPermissions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
		// /user [username/uuid] setgroup [group]
		// /user [username/uuid] meta prefix [prefix]
		// /user [username/uuid] addperm [permission]
		// /user [username/uuid] rmperm [permission]
		if (args.length < 3) {
			return false;
		}

		if (args[0].equals("setgroup")) {
			return false;
		}

		Player player;
		if (args[0].length() <= 16) {
			player = this.plugin.getServer().getPlayerExact(args[0]);
			if (player != null) {
				args[0] = player.getUniqueId().toString();
				this.handler(sender, args, player);
			} else {
				// Handle ASYNC offline crap
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
					@Override
					public void run() {
						UUID uuid = Gberry.getOfflineUUID(args[0]);
						if (uuid != null) {
							// Copy args over
							final String newArgs[] = new String[args.length];
							newArgs[0] = uuid.toString();
							for (int i = 1; i < args.length; i++) {
								newArgs[i] = args[i];
							}

							// Main thread sync
							UserCommand.this.plugin.getServer().getScheduler().runTask(UserCommand.this.plugin, new Runnable() {
								@Override
								public void run() {
									UserCommand.this.handler(sender, newArgs, null);
								}
							});
						}
					}
				});
			}
		} else if (args[0].length() == 32) {
			player = this.plugin.getServer().getPlayer(StringCommon.uuidFromStringWithoutDashes(args[0]));

			// Copy args over
			final String newArgs[] = new String[args.length];
			newArgs[0] = StringCommon.uuidFromStringWithoutDashes(args[0]).toString();
			for (int i = 1; i < args.length; i++) {
				newArgs[i] = args[i];
			}

			this.handler(sender, newArgs, player);
		} else {
			player = this.plugin.getServer().getPlayer(UUID.fromString(args[0]));
			this.handler(sender, args, player);
		}

		return true;
	}

	public void handler(CommandSender sender, String args[], final Player player) {
		// Adding or clearing prefix meta
        if (args[1].equals("debug")) {
			this.debugPermissions(args[0], sender);
        } else if (args[1].equals("meta") && args[2].equals("prefix")) {
            if (args.length >= 4) {
                String newPrefix = "";
                for (int i = 3; i < args.length; i++) {
                    newPrefix += " " + args[i];
                }
                newPrefix = newPrefix.substring(1);
                this.addPrefix(args[0], newPrefix);
            } else {
                this.addPrefix(args[0], "");
            }

			sender.sendMessage(ChatColor.GREEN + "Meta updated for " + args[0]);
		} else if (args[1].equals("addperm") || args[1].equals("rmperm")) {
			this.addRemovePerm(args[0], args[2], args[1]);

			sender.sendMessage(ChatColor.GREEN + "Permissions updated for " + args[0]);
		} else if (args[1].equals("setgroup")) {
			this.setGroup(args[0], args[2], sender);
		} else if (args[1].equals("addgroup") || args[1].equals("rmgroup")) {
			this.addRemoveSubGroup(args[0], args[2], sender, args[1]);
		} else if (args[1].equals("chkperm") || args[1].equals("checkperm")) {
		  	this.checkPermission(args[0], args[2], sender);
		} else if (args[1].equals("buygroup")) {
			this.buyGroup(args[0], args[2], sender);
		} else {
            sender.sendMessage(ChatColor.RED + "You sir, are a bk randy downy, and cannot type or think.");
            sender.sendMessage(ChatColor.GOLD + "Drink bleach and kill yourself.");
            sender.sendMessage(ChatColor.GREEN + "NOW!");
            return;
        }

		// They are online, update their permissions now
		if (player != null) {
			Map<String, Boolean> perms = this.plugin.getAllPermissionsForUser(player.getUniqueId().toString());
			BukkitCompat.setPermissions(player, plugin, perms);

			// Make sure we call this synchronously
			BukkitUtil.runTask(new Runnable() {
				@Override
				public void run() {
					// Call group change event
					GPermissions.plugin.getServer().getPluginManager().callEvent(new GroupChangeEvent(player));
				}
			});

			if (this.plugin.isSendUserMessages()) {
				player.sendMessage(ChatColor.GREEN + "Permissions updated.");
			}
		}
	}

	public void addPrefix(final String uuid, final String prefix) {
		Player player = this.plugin.getServer().getPlayer(UUID.fromString(uuid));

		if (player != null) {
			player.setMetadata("prefix", new FixedMetadataValue(this.plugin, prefix));
		}

		// Update maps/db
		GUser user = null;
		if (this.plugin.getUsers().containsKey(uuid)) {
			user = this.plugin.getUsers().get(uuid);
			user.setPrefix(prefix);
		}

		// Update DB
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			@Override
			public void run() {
				// Get User from DB (in case they logged off or something weird)
				GUser gUser = DatabaseManager.getUser(uuid);
				if (gUser == null) {
					gUser = new GUser(uuid, "default", "", new ConcurrentHashMap<String, Boolean>());
				}

				DatabaseManager.updateUser(uuid, gUser.getGroupName().toLowerCase(), prefix);
			}
		});
	}

	public void addRemovePerm(final String uuid, final String permission, final String addOrRemove) {
		GUser user;
		if (this.plugin.getUsers().containsKey(uuid)) {
			user = this.plugin.getUsers().get(uuid);

			// Update cache
			if (addOrRemove.equals("addperm")) {
				if (!user.getPermissions().containsKey(permission)) {
					user.getPermissions().put(permission, true);
				} else {
					// Uh...they already have this permission
					return;
				}
			} else if (addOrRemove.equals("rmperm")) {
				if (user.getPermissions().remove(permission) == null) {
					// Uh...they don't have this permission
					return;
				}
			}
		}

		// Sync the DB
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			@Override
			public void run() {
				// If they have a user create one
				GUser gUser = DatabaseManager.getUser(uuid);
				if (gUser == null) {
					DatabaseManager.updateUser(uuid, "default", "");
				}

				if (addOrRemove.equals("addperm")) {
					DatabaseManager.insertUserPermissions(uuid, permission);
				} else if (addOrRemove.equals("rmperm")) {
					DatabaseManager.deleteUserPermissions(uuid, permission);
				}

				if (!UserCommand.this.plugin.getTablePrefix().contains("factions")) {
					List<String> args = new ArrayList<>();
					args.add("GPermissions");
					args.add(addOrRemove);
					args.add(uuid);
					args.add(permission);
					Gberry.sendGSyncEvent(args);
				}
			}
		});
	}

	public void setGroup(final String uuid, final String groupName, CommandSender sender) {
		// First validate if the group even exists
		if (!this.plugin.getGroups().containsKey(groupName.toLowerCase())) {
			sender.sendMessage(ChatColor.RED + "No group exists.");
			return;
		}

		Group group = this.plugin.getGroups().get(groupName.toLowerCase());
		GUser user = this.plugin.getUsers().get(uuid);

		if (user != null) {
			user.setGroupName(groupName.toLowerCase());
			user.setGroup(group);
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				// Get the user's prefix if they have one
				GUser gUser = DatabaseManager.getUser(uuid);
				String prefix = "";
				if (gUser != null) {
					prefix = gUser.getPrefix();
				}

				DatabaseManager.updateUser(uuid, groupName.toLowerCase(), prefix);

				if (!UserCommand.this.plugin.getTablePrefix().contains("factions")) {
					List<String> args = new ArrayList<>();
					args.add("GPermissions");
					args.add("setgroup");
					args.add(uuid);
					args.add(groupName.toLowerCase());
					Gberry.sendGSyncEvent(args);
				}
			}

		}.runTaskAsynchronously(GPermissions.plugin);

			sender.sendMessage(ChatColor.GREEN + "Updated group for " + uuid);
	}

	public void buyGroup(final String uuid, final String groupName, CommandSender sender) {
		// First validate if the group even exists
		if (!this.plugin.getGroups().containsKey(groupName.toLowerCase())) {
			sender.sendMessage(ChatColor.RED + "No group exists.");
			return;
		}

		final String permission = "badlion." + groupName + "perm";

		Group group = this.plugin.getGroups().get(groupName.toLowerCase());
		GUser user = this.plugin.getUsers().get(uuid);

		if (user != null) {
			user.setGroupName(groupName.toLowerCase());
			user.setGroup(group);

			if (!user.getPermissions().containsKey(permission)) {
				user.getPermissions().put(permission, true);
			} else {
				// Uh...they already have this permission
				return;
			}
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				// Get the user's prefix if they have one
				GUser gUser = DatabaseManager.getUser(uuid);
				String prefix = "";
				if (gUser != null) {
					prefix = gUser.getPrefix();
				}

				DatabaseManager.updateUser(uuid, groupName.toLowerCase(), prefix);
				DatabaseManager.insertUserPermissions(uuid, permission);

				if (!UserCommand.this.plugin.getTablePrefix().contains("factions")) {
					List<String> args = new ArrayList<>();
					args.add("GPermissions");
					args.add("setgroup");
					args.add(uuid);
					args.add(groupName.toLowerCase());
					Gberry.sendGSyncEvent(args);

					args = new ArrayList<>();
					args.add("GPermissions");
					args.add("addperm");
					args.add(uuid);
					args.add(permission);
					Gberry.sendGSyncEvent(args);
				}
			}

		}.runTaskAsynchronously(GPermissions.plugin);

		sender.sendMessage(ChatColor.GREEN + "Updated group for " + uuid);
	}

	public void addRemoveSubGroup(final String uuid, final String groupName, CommandSender sender, final String action) {
		// First validate if the group even exists
		if (!this.plugin.getGroups().containsKey(groupName.toLowerCase())) {
			sender.sendMessage(ChatColor.RED + "No group exists.");
			return;
		} else if (groupName.toLowerCase().equals("default")) {
			sender.sendMessage(ChatColor.RED + "Cannot set default as a sub-group");
			return;
		}

		Group group = this.plugin.getGroups().get(groupName.toLowerCase());
		GUser user = this.plugin.getUsers().get(uuid);

		if (user != null) {
			if (action.equals("addgroup")) {
				user.getSubgroups().add(group);
			} else {
				user.getSubgroups().remove(group);
			}
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				// If they have a user create one
				GUser gUser = DatabaseManager.getUser(uuid);
				if (gUser == null) {
					DatabaseManager.updateUser(uuid, "default", "");
				}

				DatabaseManager.addRemoveSubGroup(UUID.fromString(uuid), groupName.toLowerCase(), action);

				if (!UserCommand.this.plugin.getTablePrefix().contains("factions")) {
					List<String> args = new ArrayList<>();
					args.add("GPermissions");
					args.add(action);
					args.add(uuid);
					args.add(groupName.toLowerCase());
					Gberry.sendGSyncEvent(args);
				}
			}

		}.runTaskAsynchronously(GPermissions.plugin);

		sender.sendMessage(ChatColor.GREEN + "Updated sub-groups for " + uuid);
	}

	public void debugPermissions(final String uuid, final CommandSender sender) {
		new BukkitRunnable() {
			public void run() {
		   		String username = Gberry.getUsernameFromUUID(uuid);
				GUser gUser = DatabaseManager.getUser(uuid);

				if (gUser == null) {
					sender.sendMessage(ChatColor.RED + "No permissions found");
					return;
				}

				sender.sendMessage(ChatColor.BLUE + username + ChatColor.YELLOW + " is in group " + ChatColor.GREEN + gUser.getGroupName());

				for (String groupName : DatabaseManager.getUserSubgroups(uuid)) {
					sender.sendMessage(ChatColor.BLUE + username + ChatColor.YELLOW + " is in sub-group " + ChatColor.AQUA + groupName);
				}

				if (!gUser.getPrefix().isEmpty()) {
					sender.sendMessage(ChatColor.BLUE + username + ChatColor.YELLOW + " has prefix " + ChatColor.GOLD + gUser.getPrefix());
				} else {
					sender.sendMessage(ChatColor.BLUE + username + ChatColor.YELLOW + " has no prefix cuz they're a BK RANDY");
				}

				for (String permissionName : DatabaseManager.getUserPermissions(uuid)) {
					sender.sendMessage(ChatColor.BLUE + username + ChatColor.YELLOW + " has permission " + ChatColor.RED + permissionName);
				}
			}
		}.runTaskAsynchronously(GPermissions.plugin);
	}

	public void checkPermission(final String uuid, final String permission, final CommandSender sender) {
		new BukkitRunnable() {
			public void run() {
				String username = Gberry.getUsernameFromUUID(uuid);
				GUser gUser = DatabaseManager.getUser(uuid);

				if (gUser == null) {
					sender.sendMessage(ChatColor.YELLOW + username + ChatColor.RED + " does not have permission " + ChatColor.YELLOW + permission);
					return;
				}

				Group group = GPermissions.plugin.getGroups().get(gUser.getGroupName());
				if (UserCommand.this.checkMapPermission(uuid, permission, group.getPermissions(), sender)) {
					return;
				}

				for (String groupName : DatabaseManager.getUserSubgroups(uuid)) {
					Group subgroup = GPermissions.plugin.getGroups().get(groupName);

					if (UserCommand.this.checkMapPermission(uuid, permission, subgroup.getPermissions(), sender)) {
						return;
					}
				}

				for (String permissionName : DatabaseManager.getUserPermissions(uuid)) {
					if (permissionName.equalsIgnoreCase(permission)) {
						sender.sendMessage(ChatColor.YELLOW + uuid + ChatColor.GREEN + " has permission " + ChatColor.YELLOW + permission);
					}
				}

				sender.sendMessage(ChatColor.YELLOW + uuid + ChatColor.RED + " does not have permission " + ChatColor.YELLOW + permission);
			}
		}.runTaskAsynchronously(GPermissions.plugin);
	}

    public boolean checkMapPermission(String uuid, String permission, Map<String, Boolean> map, CommandSender sender) {
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (permission.equalsIgnoreCase(entry.getKey())) {
                if (entry.getValue()) {
                    sender.sendMessage(ChatColor.YELLOW + uuid + ChatColor.GREEN + " has permission " + ChatColor.YELLOW + permission);
                } else {
                    sender.sendMessage(ChatColor.YELLOW + uuid + ChatColor.RED + " does not have permission " + ChatColor.YELLOW + permission);
                }

                return true;
            }
        }

        return false;
    }

}
