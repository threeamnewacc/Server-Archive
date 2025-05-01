package net.badlion.gpermissions;

import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gpermissions.bukkitevents.GroupChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GSyncListener implements Listener {

	private GPermissions plugin;

	public GSyncListener(GPermissions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
    public void onGSyncEvent(GSyncEvent event) {
        if (event.getArgs().size() < 4) {
            return;
        }

		// Hack we just want to see if they should be getting their local permissions
		if (this.plugin.getTablePrefix().contains("factions")) {
            String subChannel = event.getArgs().get(0);

            if (subChannel.equals("GPermissions")) {
                String msg = event.getArgs().get(1);
                String uuid = event.getArgs().get(2);

                if (msg.equals("setgroup")) {
                    String group = event.getArgs().get(3);

                    GUser gUser = this.plugin.getUsers().get(uuid);
                    if (gUser != null) {
                        if (group.equals("donator")) {
                            gUser.getPermissions().put("badlion.donator", true);
                        } else if (group.equals("donatorplus")) {
                            gUser.getPermissions().put("badlion.donator", true);
                            gUser.getPermissions().put("badlion.donatorplus", true);
                        }

                        // Is player online?
                        Player player = GPermissions.plugin.getServer().getPlayer(UUID.fromString(uuid));
                        if (player != null) {
                            // Call group change event
                            GPermissions.plugin.getServer().getPluginManager().callEvent(new GroupChangeEvent(player));
                        }
                    }
                }
            }

            return;
		}

        String subChannel = event.getArgs().get(0);
        if (subChannel.equals("GPermissions")) {
            String msg = event.getArgs().get(1);
            String uuid = event.getArgs().get(2);

            GUser gUser = this.plugin.getUsers().get(uuid);
            if (gUser != null) {
                if (msg.equals("setgroup")) {
                    String group = event.getArgs().get(3);

                    gUser.setGroupName(group);
                    gUser.setGroup(this.plugin.getGroups().get(group));

                    // Check if we need to update their player object
                    SuperPermissionListener.setupPlayerIfOnline(UUID.fromString(uuid));
                } else if (msg.equals("addgroup") || msg.equals("rmgroup")) {
                    String groupName = event.getArgs().get(3);
                    Group group = this.plugin.getGroups().get(groupName.toLowerCase());

                    // Update cache
                    if (msg.equals("addgroup")) {
                        gUser.getSubgroups().add(group);
                    } else {
                        gUser.getSubgroups().remove(group);
                    }

                    // Check if we need to update their player object
                    SuperPermissionListener.setupPlayerIfOnline(UUID.fromString(uuid));
                } else if (msg.equals("addperm") || msg.equals("rmperm")) {
                    String permission = event.getArgs().get(3);

                    if (msg.equals("addperm")) {
                        gUser.getPermissions().put(permission, true);
                    } else {
                        gUser.getPermissions().remove(permission);
                    }

                    // Check if we need to update their player object
                    SuperPermissionListener.setupPlayerIfOnline(UUID.fromString(uuid));
                }
            }
        }
	}

}
