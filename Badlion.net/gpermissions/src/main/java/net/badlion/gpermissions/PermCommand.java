package net.badlion.gpermissions;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PermCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        // /perm create [group] [prefix] - creates a group
        // /perm add [group] [perm] - adds a certain permission to a group
        // /perm remove [group] [perm] - removes a certain permission from a group
        // /perm reload - reloads the groups from the DB
        new BukkitRunnable() {
            public void run() {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        GPermissions.getInstance().loadGroups();
                        GPermissions.getInstance().getSuperPermissionListener().setupAllPlayers();

                        for (GUser user : GPermissions.getInstance().getUsers().values()) {
                            String uuid = user.getUuid();
                            // Find and attach the group
                            for (Group group : GPermissions.getInstance().getGroups().values()) {
                                if (group.getName().equals(user.getGroupName())) {
                                    user.setGroup(group);
                                }
                            }

                            // Integrity error
                            try {
                                if (user.getGroup() == null) {
                                    throw new Exception("GPermission integrity error with " + uuid);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Get SubGroups and Permissions for user
                            Set<String> rawPermissions = DatabaseManager.getUserPermissions(uuid);
                            Set<String> subgroups = DatabaseManager.getUserSubgroups(uuid);

                            // Empty Permission map
                            Map<String, Boolean> permissions = new ConcurrentHashMap<>();

                            if (!rawPermissions.isEmpty()) {
                                for (String rawPermission : rawPermissions) {
                                    // Don't over-ride sub-groups
                                    if (!permissions.containsKey(rawPermission)) {
                                        if (rawPermission.startsWith("^")) {
                                            permissions.put(rawPermission.substring(1), false);
                                        } else {
                                            // Add * and regular perms as true...we can handle this elsewhere
                                            permissions.put(rawPermission, true);
                                        }
                                    }
                                }
                            }

                            // Always set permissions because we set null above for the map (even if empty)
                            user.setPermissions(permissions);

                            // Do we have any subgroups?
                            if (subgroups != null) {
                                for (String sg : subgroups) {
                                    Group g = GPermissions.getInstance().getGroups().get(sg.toLowerCase());
                                    if (g != null) {
                                        user.getSubgroups().add(g);
                                    }
                                }
                            }

                            // Store user
                            GPermissions.getInstance().addUser(uuid, user);
                        }
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("debug")) {
                        String groupName = args[1].toLowerCase();
                        Group group = DatabaseManager.getGroup(groupName);
                        if (group == null) {
                            sender.sendMessage(ChatColor.RED + "No group exists");
                        } else {
                            Set<String> permissions = DatabaseManager.getGroupPermissions(groupName);
                            sender.sendMessage(ChatColor.GREEN + "Group Name: " + ChatColor.GOLD + groupName);
                            sender.sendMessage(ChatColor.GREEN + "Group Prefix: " + ChatColor.GOLD + group.getPrefix());
                            for (String permission : permissions) {
                                sender.sendMessage(ChatColor.BLUE + groupName + org.bukkit.ChatColor.YELLOW + " has permission " + ChatColor.AQUA + permission);
                            }
                        }

                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("update")) {
                        String group = args[1].toLowerCase();
                        String prefix = args[2];
                        DatabaseManager.insertUpdateGroup(group, prefix);
                        sender.sendMessage(ChatColor.GREEN + "Group created");
                    } else if (args[0].equalsIgnoreCase("add")) {
                        String group = args[1].toLowerCase();
                        String perm = args[2].toLowerCase();
                        // TODO: Make sure group/perm exist/don't already exist
                        DatabaseManager.insertGroupPermissions(group, perm);
                        sender.sendMessage(ChatColor.GREEN + "Group permissions updated");
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        String group = args[1].toLowerCase();
                        String perm = args[2].toLowerCase();
                        // TODO: Make sure group/perm exist/don't already exist
                        DatabaseManager.deleteGroupPermissions(group, perm);
                        sender.sendMessage(ChatColor.GREEN + "Group permissions updated");
                    }
                }
            }
        }.runTaskAsynchronously(GPermissions.plugin);

        return true;
    }

}
