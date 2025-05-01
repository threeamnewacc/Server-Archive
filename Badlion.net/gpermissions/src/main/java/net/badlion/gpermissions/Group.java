package net.badlion.gpermissions;

import org.bukkit.ChatColor;

import java.util.Map;

public class Group {

	private String name;
	private String prefix;
	private Map<String, Boolean> permissions;

	public Group(String name, String prefix, Map<String, Boolean> permissions) {
		this.name = name;
		this.prefix = prefix;
		this.permissions = permissions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return ChatColor.RESET + prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, Boolean> permissions) {
		this.permissions = permissions;
	}
}
