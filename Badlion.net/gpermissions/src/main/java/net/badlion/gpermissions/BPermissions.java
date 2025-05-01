package net.badlion.gpermissions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BPermissions {

	public static void importBPermissions() {
		// Try to read a bPermissions file
		File groupsFile = new File("plugins/bPermissions/groups.yml");

		if (!groupsFile.exists()) {
			Bukkit.getLogger().severe("no config file found for bPermissions.");
			return;
		}

		YamlConfiguration groupConfig = YamlConfiguration.loadConfiguration(groupsFile);

		if (groupConfig == null) {
			Bukkit.getLogger().severe("no file configuration found.");
			return;
		}

		Set<String> groups = groupConfig.getConfigurationSection("groups").getKeys(false);
		for (String group : groups) {
			// Get group prefix
			String prefix = groupConfig.getString("groups." + group + ".meta.prefix", "");

			// Add this group to the database
			DatabaseManager.insertUpdateGroup(group, prefix);

			// Get all of their permissions
			List<String> permissions = groupConfig.getStringList("groups." + group + ".permissions");
			for (String permission : permissions) {
				DatabaseManager.insertGroupPermissions(group, permission);
			}
		}

		// Get individual permissions now
		File usersFile = new File("plugins/bPermissions/users.yml");

		if (!usersFile.exists()) {
			Bukkit.getLogger().severe("no config file found for bPermissions.");
			return;
		}

		YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(usersFile);

		if (userConfig == null) {
			Bukkit.getLogger().severe("no file configuration found.");
			return;
		}

		Set<String> users = userConfig.getConfigurationSection("users").getKeys(false);
		for (String user : users) {
			// Get user prefix
			String prefix = userConfig.getString("users." + user + ".meta.prefix", "");

			// Get user group
			String group = userConfig.getStringList("users." + user + ".groups").get(0);

			// Get all of their permissions
			boolean hasPermissions = false;
			List<String> permissions = userConfig.getStringList("users." + user + ".permissions");
			for (String permission : permissions) {
				hasPermissions = true;
				DatabaseManager.insertUserPermissions(user, permission);
			}

			// This is just a regular user, don't bother inserting a record for them
			if (group.equals("default") && !hasPermissions) {
				continue;
			}

			// Add user to the database (even if default, cuz they have permissions)
			DatabaseManager.updateUser(user, group, prefix);
		}
	}

}
