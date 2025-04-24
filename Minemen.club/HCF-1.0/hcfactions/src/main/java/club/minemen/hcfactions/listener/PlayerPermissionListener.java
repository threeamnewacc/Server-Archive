package club.minemen.hcfactions.listener;

import club.minemen.core.CorePlugin;
import club.minemen.core.mineman.Mineman;
import club.minemen.core.rank.Rank;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.struct.Permission;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

/**
 * @since 12/3/2017
 */
@RequiredArgsConstructor
public class PlayerPermissionListener implements Listener {

	private final HCFactions plugin;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoinAfterScoreboardSet(PlayerJoinEvent event) {
		// Update their own scoreboard when they join so they getAll put into a team.
		FactionPlayer factionPlayer = FPlayers.getInstance().get(event.getPlayer());
		factionPlayer.updatePlayerTeam(event.getPlayer());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

		PermissionAttachment permissionAttachment = player.addAttachment(this.plugin);
		Map<String, Boolean> permissions = new HashMap<>();

		if (mineman.hasRank(Rank.ADMIN)) {

		}

		if (mineman.hasRank(Rank.MODPLUS)) {
			permissions.put("coreprotect.rollback", true);
			permissions.put("coreprotect.restore", true);
		}
		if (mineman.hasRank(Rank.MOD)) {
			permissions.put("coreprotect.inspect", true);
			permissions.put("coreprotect.lookup", true);
		}

		/*permissions.put(Permission.ENABLE.node, true);
		permissions.put(Permission.MOTD.node, true);
		permissions.put(Permission.STUCK.node, true);*/

		permissions.forEach(permissionAttachment::setPermission);

		/*if (player.hasPermission("badlion.fmod")) {
			GPermissions.giveModPermissions(player);
		} else if (player.hasPermission("badlion.ftrial")) {
			GPermissions.giveTrialPermissions(player);
		}

		Map<String, Boolean> perms = GPermissions.plugin.getAllPermissionsForUser(player.getUniqueId().toString());
		perms.put(Permission.ENABLE.node, true);
		perms.put(Permission.TIME.node, true);
		perms.put(Permission.MOTD.node, true);
		perms.put(Permission.STUCK.node, true);
		if (perms.containsKey("badlion.admin")) {
			perms.put("kmod.toggle", true);
			perms.put("kmod.vanish", true);
			perms.put("kmod.forced", true);
			perms.put("kmod.inspect.command", true);
			perms.put("kmod.inspect.action", true);
			perms.put("kspawn.spawn", true);
			perms.put("kspawn.bypasswarmup", true);
			perms.put("factions.staff", true);
			perms.put("nightmare.mod", true);
			perms.put("bukkit.command.teleport", true);
			perms.put("hints.bypass", true);
			perms.put("factions.logger.bypass", true);
			perms.put("factions.admin.any", true);
			perms.put("kmod.creative", true);
			perms.put("kmod.creative.inventory", true);
			perms.put("kmod.creative.build", true);
			//Temp fix for shitty prism plugin maybe?
			for (String string : getShittyPrismPerms()) {
				perms.put(string, true);
			}
			perms.put("worldedit.*", true);
			perms.put("factions.butcher", true);
			perms.put("factions.bypass", true);
			perms.put("factions.revive", true);
			perms.put("bukkit.command.gamemode", true);
			perms.put("factions.setdtr", true);
			perms.put("kevent.manhunt", true);
			perms.put("AWE.user.mode.change", true);

			//Koth
			perms.put("koth.admin.start", true);
			perms.put("koth.admin.stop", true);
			perms.put("koth.admin.seecapper", true);
			perms.put("lootkeys.spawnkey", true);

			perms.put("worldguard.region.bypass.world", true);
			perms.put("worldguard.region.bypass.world_nether", true);
			perms.put("worldguard.region.bypass.world_the_end", true);

			HCFactions.getInstance().getLogger().info(player.getName() + " has admin perms");
		} else if (perms.containsKey("badlion.kohimngr")) {
			perms.put("kmod.toggle", true);
			perms.put("kmod.vanish", true);
			perms.put("kmod.forced", true);
			perms.put("kmod.inspect.command", true);
			perms.put("kmod.inspect.action", true);
			perms.put("kspawn.spawn", true);
			perms.put("kspawn.bypasswarmup", true);
			perms.put("factions.staff", true);
			perms.put("nightmare.mod", true);
			perms.put("bukkit.command.teleport", true);
			perms.put("hints.bypass", true);
			perms.put("factions.logger.bypass", true);
			perms.put("factions.admin.any", true);
			perms.put("kmod.creative", true);
			perms.put("kmod.creative.inventory", true);
			perms.put("kmod.creative.build", true);
			//Temp fix for shitty prism plugin maybe?
			for (String string : getShittyPrismPerms()) {
				perms.put(string, true);
			}
			perms.put("worldedit.*", true);
			perms.put("factions.butcher", true);
			perms.put("factions.bypass", true);
			perms.put("factions.revive", true);
			perms.put("bukkit.command.gamemode", true);
			perms.put("factions.setdtr", true);
			perms.put("AWE.user.mode.change", true);

			//Koth
			perms.put("koth.admin.start", true);
			perms.put("koth.admin.stop", true);
			perms.put("koth.admin.seecapper", true);
			perms.put("lootkeys.spawnkey", true);

			perms.put("worldguard.region.bypass.world", true);
			perms.put("worldguard.region.bypass.world_nether", true);
			perms.put("worldguard.region.bypass.world_the_end", true);

			HCFactions.getInstance().getLogger().info(player.getName() + " has fmngr perms");
		} else if (perms.containsKey("badlion.fsenior")) {
			perms.put("kmod.toggle", true);
			perms.put("kmod.vanish", true);
			perms.put("kmod.forced", true);
			perms.put("kmod.inspect.command", true);
			perms.put("kmod.inspect.action", true);
			perms.put("kspawn.spawn", true);
			perms.put("kspawn.bypasswarmup", true);
			perms.put("factions.staff", true);
			perms.put("nightmare.mod", true);
			perms.put("bukkit.command.teleport", true);
			perms.put("hints.bypass", true);
			perms.put("factions.logger.bypass", true);
			perms.put("prism.help", true);
			perms.put("prism.wand.inspect", true);
			perms.put("factions.admin.any", true);
			perms.put("kmod.creative", true);
			perms.put("factions.revive", true);

			HCFactions.getInstance().getLogger().info(player.getName() + " has fsenior perms");
		} else if (perms.containsKey("badlion.fmod")) {
			perms.put("kmod.vanish", true);
			perms.put("kmod.forced", true);
			perms.put("kmod.inspect.command", true);
			perms.put("kmod.inspect.action", true);
			perms.put("kspawn.spawn", true);
			perms.put("kspawn.bypasswarmup", true);
			perms.put("factions.staff", true);
			perms.put("nightmare.mod", true);
			perms.put("bukkit.command.teleport", true);
			perms.put("hints.bypass", true);
			perms.put("factions.logger.bypass", true);
			perms.put("factions.revive", true);


			// Mod Perms
			perms.put("bm.kick", Boolean.valueOf(true));
			perms.put("bm.ban", Boolean.valueOf(true));
			perms.put("bm.unban", Boolean.valueOf(true));
			perms.put("gberry.invsee", Boolean.valueOf(true));
			perms.put("badlion.gm", Boolean.valueOf(true));

			HCFactions.getInstance().getLogger().info(player.getName() + " has mod perms");
		} else if (perms.containsKey("badlion.ftrial")) {
			perms.put("kmod.vanish", true);
			perms.put("kmod.forced", true);
			perms.put("kmod.inspect.command", true);
			perms.put("kmod.inspect.action", true);
			perms.put("kspawn.spawn", true);
			perms.put("kspawn.bypasswarmup", true);
			perms.put("factions.staff", true);
			perms.put("nightmare.mod", true);
			perms.put("bukkit.command.teleport", true);
			perms.put("hints.bypass", true);
			perms.put("factions.logger.bypass", true);
			HCFactions.getInstance().getLogger().info(player.getName() + " has ftrial perms");
		}
		BukkitCompat.setPermissions(player, GPermissions.plugin, perms);*/
	}
}
