package net.badlion.gpermissions;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class
GPermissions extends JavaPlugin {

	public static GPermissions plugin;
	public static GPermissions getInstance() {
		return GPermissions.plugin;
	}
	private GSyncListener GSyncListener;
	private SuperPermissionListener superPermissionListener;

	private boolean sendUserMessages;

	private String tablePrefix = "default";

	private Map<String, Group> groups = new ConcurrentHashMap<>();
	private Map<String, GUser> users = new ConcurrentHashMap<>();

	public GPermissions() {
		GPermissions.plugin = this;
	}

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		this.sendUserMessages = this.getConfig().getBoolean("gpermissions.send_user_messages");

		// Register BungeeCord
		this.GSyncListener = new GSyncListener(this);

		// Register BungeeCord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getPluginManager().registerEvents(GSyncListener, this);

		this.tablePrefix = this.getConfig().getString("gpermissions.server");
		if (this.tablePrefix.equals("default")) {
			Bukkit.getLogger().severe("Default name specified, disabling GPermisisons!!!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

        // Only fetch data if we need it (DAMN YOU AUSSIES)
        Bukkit.getLogger().info("XXX " + this.tablePrefix);
        if (this.tablePrefix.contains("factions")) {
            Bukkit.getLogger().info("Detected factions server");
        }

	   	this.loadGroups();

		this.superPermissionListener = new SuperPermissionListener(this);
		this.getServer().getPluginManager().registerEvents(this.superPermissionListener, this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

		this.getCommand("user").setExecutor(new UserCommand(this));
		this.getCommand("perm").setExecutor(new PermCommand());
	}

	@Override
	public void onDisable() {

	}

	public String getUserGroupMetaPrefix(UUID uuid) {
		return this.getUserGroupMetaPrefix(uuid, false);
	}

	public String getUserGroupMetaPrefix(UUID uuid, boolean disguisePrefix) {
		Player player = Bukkit.getPlayer(uuid);

		// Return no prefix if player is disguised
		if (!disguisePrefix && player.isDisguised()) {
			return "";
		}

		return this.getUserMeta(uuid, "prefix") + this.getUserGroup(uuid).getPrefix();
	}

	public Group getUserGroup(UUID uuid) {
		if (this.getUsers().containsKey(uuid.toString().toLowerCase())) {
			return this.getUsers().get(uuid.toString().toLowerCase()).getGroup();
		}

		return this.getGroups().get("default");
	}

	public String getUserMeta(UUID uuid, String value) {
		return this.getUserMeta(uuid, value, true);
	}

	public String getUserMeta(UUID uuid, String value, boolean hidePrefixWhenDisguised) {
		Player player = Bukkit.getPlayer(uuid);

		// Return no prefix if player is disguised
		// NOTE: Null check needed because we call this from login event
		if (hidePrefixWhenDisguised && player != null && player.isDisguised()) {
			return "";
		}

		if (this.users.containsKey(uuid.toString().toLowerCase())) {
			// Hack, load default group
			GUser user = this.users.get(uuid.toString().toLowerCase());
			return value.equals("prefix") ? ChatColor.RESET + user.getPrefix() : ""; // TODO: Suffix
		}

		return "";
	}

	public String getGroupMeta(String groupName, String value) {
		if (this.groups.containsKey(groupName.toLowerCase())) {
			// Hack, load default group
			Group group = this.groups.get(groupName.toLowerCase());
			return value.equals("prefix") ? group.getPrefix() : ""; // TODO: Suffix
		}

		return "";
	}

	public boolean groupHasPermission(String groupName, String permission) {
		Group group = null;
		GUser user = null;

		if (!this.users.containsKey(groupName.toLowerCase())) {
			// Hack, load default group
			group = this.groups.get("default");
		} else {
			user = this.users.get(groupName.toLowerCase());
			group = user.getGroup();
		}

		return hasPermissionParser(group.getPermissions(), permission);
	}

	public String getUserGroupOffline(String uuid) {
		Gberry.catchNonAsyncThread();

		GUser gUser = DatabaseManager.getUser(uuid);
		if (gUser == null) {
			return "default";
		}

		return gUser.getGroupName();
	}

	public boolean hasUserPermissionOffline(String uuid, String permission) {
		Gberry.catchNonAsyncThread();

		return DatabaseManager.getUserPermissions(uuid).contains(permission);
	}

	public boolean userHasPermission(String uuid, String permission) {
		GUser user = this.users.get(uuid.toLowerCase());

		if (user != null) {
			// Check group permissions first
			if (!hasPermissionParser(user.getGroup().getPermissions(), permission)) {
				// Check Sub-Groups next
				for (Group subgroup : user.getSubgroups()) {
					if (hasPermissionParser(subgroup.getPermissions(), permission)) {
						return true;
					}
				}

				// Check individual permissions last
				return hasPermissionParser(user.getPermissions(), permission);
			}

			// We found the permission in the group permissions
			return true;
		} else {
			// Default user
			return hasPermissionParser(this.groups.get("default").getPermissions(), permission);
		}
	}

	public Map<String, Boolean> getAllPermissionsForUser(String uuid) {
		Group group = null;
		GUser user = null;

		if (!this.users.containsKey(uuid.toLowerCase())) {
			// Hack, load default group
			group = this.groups.get("default");
		} else {
			user = this.users.get(uuid.toLowerCase());
			group = user.getGroup();
		}

		Map<String, Boolean> map = new HashMap<>();
		for (Map.Entry<String, Boolean> permissions : group.getPermissions().entrySet()) {
			map.put(permissions.getKey(), permissions.getValue());
		}

		if (user != null) {
			// Sub-groups
			for (Group group1 : user.getSubgroups()) {
				for (Map.Entry<String, Boolean> permissions : group1.getPermissions().entrySet()) {
					if (!map.containsKey(permissions.getKey())) {
						map.put(permissions.getKey(), permissions.getValue());
					}
				}
			}

			// Regular permissions
			for (Map.Entry<String, Boolean> permissions : user.getPermissions().entrySet()) {
				if (!map.containsKey(permissions.getKey())) {
					map.put(permissions.getKey(), permissions.getValue());
				}
			}
		}

		// Factions
		if (Gberry.serverName.contains("factions")) {
			Boolean val = map.get("badlion.fmod");
			if (val == null || !val) {
				map.put("worldedit.navigation.thru.tool", false);
				map.put("worldedit.navigation.jumpto.tool", false);
			}
		}

		return map;
	}

	public boolean hasPermissionParser(Map<String, Boolean> permissions, String permissionNode) {
		if (permissionNode == null) {
			return false;
		}

		// Cool found it on the first try...
		// The ^ case is already handled here
		if (permissions.containsKey(permissionNode)) {
			return permissions.get(permissionNode);
		}

		// Nope...let's see if it's a *
		// Stole this from: https://github.com/rymate1234/bPermissions/blob/2.12/src/de/bananaco/bpermissions/api/Calculable.java
		// Saves me a bit of a headache
		String permission = permissionNode;
		int index = permission.lastIndexOf('.');
		while (index >= 0) {
			permission = permission.substring(0, index);
			String wildcard = permission + ".*";
			if (permissions.containsKey(wildcard)) {
				return permissions.get(wildcard);
			}
			index = permission.lastIndexOf('.');
		}

		if (permissions.containsKey("*")) {
			return permissions.get("*");
		}

		return false;
	}

	public static void giveTrialPermissions(Player player) {
		Map<String, Boolean> perms = GPermissions.plugin.getAllPermissionsForUser(player.getUniqueId().toString());

		perms.put("bm.mute", true);
		perms.put("bm.unmute", true);
		perms.put("bm.kick", true);
		perms.put("gberry.invsee", true);

		BukkitCompat.setPermissions(player, GPermissions.plugin, perms);
	}

	public static void giveModPermissions(Player player) {
		Map<String, Boolean> perms = GPermissions.plugin.getAllPermissionsForUser(player.getUniqueId().toString());

		perms.put("bm.kick", true);
		perms.put("bm.ban", true);
		perms.put("bm.unban", true);
		perms.put("gberry.invsee", true);
		perms.put("badlion.gm", true);

		BukkitCompat.setPermissions(player, GPermissions.plugin, perms);
	}

	public void loadGroups() {
		// Ok get our caches setup
		HashSet<Group> groups = DatabaseManager.getAllGroups();
		Map<String, Set<String>> groupPermissions = DatabaseManager.getAllGroupPermissions();
		for (Group group : groups) {
			// Handle ^ and * crap
			Set<String> rawPermissions = groupPermissions.get(group.getName());

			// Should never happen but just in case
			if (rawPermissions == null) {
				continue;
			}

			Map<String, Boolean> permissions = new HashMap<>();

			for (String rawPermission : rawPermissions) {
				if (rawPermission.startsWith("^")) {
					permissions.put(rawPermission.substring(1), false);
				} else {
					// Add * and regular perms as true...we can handle this elsewhere
					permissions.put(rawPermission, true);
				}
			}

			group.setPermissions(permissions);

			this.groups.put(group.getName(), group);
		}
	}

	public boolean isSendUserMessages() {
		return sendUserMessages;
	}

	public void setSendUserMessages(boolean sendUserMessages) {
		this.sendUserMessages = sendUserMessages;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public Map<String, Group> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}

	public GUser getUser(String uuid) {
		return this.users.get(uuid.toLowerCase());
	}

	public Map<String, GUser> getUsers() {
		return users;
	}

	public void setUsers(Map<String, GUser> users) {
		this.users = users;
	}

	public GSyncListener getGSyncListener() {
		return GSyncListener;
	}

	public SuperPermissionListener getSuperPermissionListener() {
		return superPermissionListener;
	}

	public void addUser(String uuid, GUser user) {
		this.users.put(uuid, user);
	}

	public void removeUser(String uuid) {
		this.users.remove(uuid);
	}
}
