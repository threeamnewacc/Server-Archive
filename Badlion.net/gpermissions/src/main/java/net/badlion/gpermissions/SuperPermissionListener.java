package net.badlion.gpermissions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles all the superperms registering/unregistering for
 * PermissionAttachments (it's basically just somewhere to stick all the nasty
 * SuperPerms stuff that wouldn't exist if SuperPerms was a more flexible
 * system.
 *
 * What's wrong with a PermissionProvider interface where we can register a
 * single PermissionProvider?!
 */
public class SuperPermissionListener implements Listener {

	private GPermissions plugin;

	/**
	 * This is put in place until such a time as Bukkit pull 466 is implemented
	 * https://github.com/Bukkit/Bukkit/pull/466
	 */
	public synchronized static void setPermissions(final Permissible p, final Plugin plugin, final Map<String, Boolean> perm) {
		BukkitCompat.setPermissions(p, plugin, perm);
	}

	// Main constructor
	protected SuperPermissionListener(GPermissions plugin) {
		this.plugin = plugin;
		// This next bit is simply to make gPermissions.* work with superperms, since I now have my bulk adding, I will concede to this
		Map<String, Boolean> children = new HashMap<String, Boolean>();
		children.put("gPermissions.admin", true);
		Permission permission = new Permission("gPermissions.*", PermissionDefault.OP, children);
		if (plugin.getServer().getPluginManager().getPermission("gPermissions.*") == null) {
			plugin.getServer().getPluginManager().addPermission(permission);
		}
	}

	/**
	 * A guaranteed way to setup all players in the server in one fell swoop
	 */
	public void setupAllPlayers() {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			setupPlayer(player);
		}
	}

	/**
	 * Set up the Player via the specified World object (note this is a
	 * bPermissions world, not a Bukkit world)
	 *
	 * @param player
	 */
	public void setupPlayer(Player player) {
		if (!plugin.isEnabled()) {
			return;
		}

		long time = System.currentTimeMillis();
		// Grab the pre-calculated effectivePermissions from the User object
		// Then whack it onto the player
		// TODO wait for the bukkit team to get their finger out, we'll use our reflection here!
		Map<String, Boolean> perms = this.plugin.getAllPermissionsForUser(player.getUniqueId().toString());
		setPermissions(player, plugin, perms);

		// Set the metadata?
		String prefix = this.plugin.getUserMeta(player.getUniqueId(), "prefix");
		String suffix = this.plugin.getUserMeta(player.getUniqueId(), "suffix");
		// WTF
		player.setMetadata("prefix", new FixedMetadataValue(this.plugin, prefix));
		player.setMetadata("suffix", new FixedMetadataValue(this.plugin, suffix));
	}

	@EventHandler(priority=EventPriority.FIRST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		// Likewise, in theory this should be all we need to detect when a player joins
		// Don't setup their player if the server is full anyways
		if (Bukkit.getCurrentPlayers() < Bukkit.getMaxPlayers() || GPermissions.plugin.userHasPermission(event.getPlayer().getUniqueId().toString(), "badlion.donator")) {
			setupPlayer(event.getPlayer());
		}
	}

	public void setupPlayer(String name) {
		if (Bukkit.getPlayer(name) != null) {
			Player player = Bukkit.getPlayer(name);
			this.setupPlayer(player);
		}
	}

	public static void setupPlayerIfOnline(UUID uuid) {
		Player p = GPermissions.plugin.getServer().getPlayer(uuid);
		if (p != null) {
			// Force re-attach their permissions
			GPermissions.plugin.getSuperPermissionListener().setupPlayer(p);
			p.sendMessage(ChatColor.GREEN + "Permissions updated");
		}
	}

}
