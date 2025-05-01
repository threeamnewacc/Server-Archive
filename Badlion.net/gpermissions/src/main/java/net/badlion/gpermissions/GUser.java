package net.badlion.gpermissions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GUser {

	private String uuid;
	private String groupName;
	private String prefix;
	private Map<String, Boolean> permissions;
	private Group group;
	private Set<Group> subgroups = new HashSet<>();

	public GUser(String uuid, String groupName, String prefix, Map<String, Boolean> permissions) {
		this.uuid = uuid;
		this.groupName = groupName;
		this.prefix = prefix;
		this.permissions = permissions;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, Boolean> permissions) {
		this.permissions = permissions;
	}

	public Set<Group> getSubgroups() {
		return subgroups;
	}

	public void setSubgroups(Set<Group> subgroups) {
		this.subgroups = subgroups;
	}
}
