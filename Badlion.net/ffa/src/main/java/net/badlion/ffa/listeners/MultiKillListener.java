package net.badlion.ffa.listeners;

import net.badlion.ffa.FFA;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiKillListener implements Listener {

	private static MultiKillListener instance;

	private Map<UUID, UUID> mapOfLastDamage = new HashMap<>();
	private Map<UUID, Long> lastDamageTime = new HashMap<>();
	private Map<UUID, Long> lastKillTime = new HashMap<>();
	private Map<UUID, Integer> lastMultiKill = new HashMap<>();
	private Map<UUID, BukkitTask> multiKillTaskMap = new HashMap<>();

	public MultiKillListener() {
		MultiKillListener.instance = this;
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = this.getKiller(player);

		if (killer != null) {
			MPGPlayer killedMPGPlayer = MPGPlayerManager.getMPGPlayer(event.getEntity().getUniqueId());

			// Did the player have a kill streak over 5?
			if (killedMPGPlayer.getCurrentKillStreak() >= 5) {
				Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + killer.getDisguisedName() + ChatColor.DARK_AQUA
						+ " has ended " + ChatColor.YELLOW + player.getDisguisedName() + ChatColor.DARK_AQUA + "'s killstreak of "
						+ ChatColor.GOLD + killedMPGPlayer.getCurrentKillStreak() + ChatColor.DARK_AQUA + "!");
			}
		}

		// Is there a killer?
		if (killer != null) {
			// Multikill messages
			this.handleMultiKill(killer);
		}

		this.mapOfLastDamage.remove(event.getEntity().getUniqueId());
		this.lastDamageTime.remove(event.getEntity().getUniqueId());
		this.lastMultiKill.remove(event.getEntity().getUniqueId());
		this.lastKillTime.remove(event.getEntity().getUniqueId());
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		this.mapOfLastDamage.remove(event.getPlayer().getUniqueId());
		this.lastDamageTime.remove(event.getPlayer().getUniqueId());
		this.lastKillTime.remove(event.getPlayer().getUniqueId());
		this.lastMultiKill.remove(event.getPlayer().getUniqueId());
		this.multiKillTaskMap.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LASTEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		this.mapOfLastDamage.remove(player.getUniqueId());
		this.lastDamageTime.remove(player.getUniqueId());
		this.lastKillTime.remove(player.getUniqueId());
		this.lastMultiKill.remove(player.getUniqueId());
		this.multiKillTaskMap.remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Player player = null;
		if (damager instanceof Projectile) {
			player = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
		} else if (damager instanceof Player) {
			player = (Player) damager;
		}

		if (player == null) return;

		// Don't track damage that we did to ourselves
		if (player != event.getEntity()) {
			this.mapOfLastDamage.put(event.getEntity().getUniqueId(), player.getUniqueId());
			this.lastDamageTime.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		this.lastDamageTime.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
	}

	private Player getKiller(Player player) {
		UUID lastDamage = this.mapOfLastDamage.get(player.getUniqueId());
		if (lastDamage != null) {
			return FFA.getInstance().getServer().getPlayer(lastDamage);
		}

		return null;
	}

	private void handleMultiKill(final Player player) {
		Long lastKillTime = this.lastKillTime.get(player.getUniqueId());
		if (lastKillTime != null) {
			// They already have a multi-kill
			int kills = this.lastMultiKill.get(player.getUniqueId());

			// Cancel old task
			this.multiKillTaskMap.get(player.getUniqueId()).cancel();

			String message = "";
			if (kills == 1) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "DOUBLE-KILL" + ChatColor.AQUA + "!";
			} else if (kills == 2) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "TRIPLE-KILL" + ChatColor.AQUA + "!";
			} else if (kills == 3) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "QUADRA-KILL" + ChatColor.AQUA + "!";
			} else if (kills == 4) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "PENTA-KILL" + ChatColor.AQUA + "!!!";
			} else if (kills == 5) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "HEXA-KILL" + ChatColor.AQUA + "!!!";
			} else if (kills == 6) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "HEPTA-KILL" + ChatColor.AQUA + "!!!";
			} else if (kills == 7) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "OCTA-KILL" + ChatColor.AQUA + "!!!";
			} else if (kills == 8) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "NONA-KILL" + ChatColor.AQUA + "!!!";
			} else if (kills == 9) {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "DECA-KILL" + ChatColor.AQUA + "!!!";
			} else {
				message += ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "MULTI-KILL" + ChatColor.AQUA + "!!! (More than 10 kills)";
			}

			Gberry.broadcastMessageNoBalance(message);

			// Add one kill for future messages
			this.lastMultiKill.put(player.getUniqueId(), kills + 1);
		} else {
			this.lastMultiKill.put(player.getUniqueId(), 1);
		}

		// Add new last kill time
		this.lastKillTime.put(player.getUniqueId(), System.currentTimeMillis());

		// Always start off a new task
		// This was causing a memory leak because the task was never being removed from the map -Travis
		this.multiKillTaskMap.put(player.getUniqueId(), BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				MultiKillListener.this.lastKillTime.remove(player.getUniqueId());
				MultiKillListener.this.lastMultiKill.remove(player.getUniqueId());
			}
		}, 20 * 10)); // 10 seconds
	}

	public static MultiKillListener getInstance() {
		return MultiKillListener.instance;
	}

	public void insertLastDamageTime(UUID uuid) {
		this.lastDamageTime.put(uuid, System.currentTimeMillis());
	}

	public Long getLastDamageTime(UUID uuid) {
		return this.lastDamageTime.get(uuid);
	}

}
