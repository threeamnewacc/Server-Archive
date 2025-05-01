package net.badlion.gpermissions;


import net.badlion.gberry.Gberry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {

    private GPermissions plugin;

    public PlayerListener(GPermissions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        String uuid = event.getUniqueId().toString();
        GUser user = DatabaseManager.getUser(uuid);

        if (user != null) {
            // Find and attach the group
            for (Group group : this.plugin.getGroups().values()) {
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
            Set<String> rawPermissions =  DatabaseManager.getUserPermissions(uuid);
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
                    Group g = this.plugin.getGroups().get(sg.toLowerCase());
                    if (g != null) {
                        user.getSubgroups().add(g);
                    }
                }
            }

            // Store user
            this.plugin.addUser(uuid, user);
        }

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.plugin.removeUser(event.getPlayer().getUniqueId().toString());
    }

}
