package com.massivecraft.factions.listeners;

import club.minemen.hcfactions.HCFactions;
import club.minemen.hcfactions.combat.events.CombatTagCreateEvent;
import club.minemen.hcfactions.combat.events.CombatTagDamageEvent;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.type.FactionsLoggerNPC;
import com.massivecraft.factions.type.LogoutTask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoggerListener implements Listener {

	private final FactionsEntityListener entityListener = new FactionsEntityListener();
	private final ProtectionListener protectionListener = new ProtectionListener();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		FactionPlayer fplayer = FPlayers.getInstance().get(event.getPlayer());

		if (HCFactions.rebooting) {
			HCFactions.getInstance().getCombatTagManager().removeCombatTagged(event.getPlayer());
			return;
		}

		// Don't spawn logger if they used /logout command
		LogoutTask task = HCFactions.getInstance().getLogoutTasks().get(event.getPlayer());
		if (task != null) {
			if (task.getCountdown() <= 0) {
				HCFactions.getInstance().getLogger().info("onPlayerQuit: " + event.getPlayer().getName() + " logged out safely");
				HCFactions.getInstance().getCombatTagManager().removeCombatTagged(event.getPlayer());
				return;
			}
			task.cancel();
		}

		// no loggers every for staff, creative, pvp prot or safezone
		if (event.getPlayer().hasPermission("factions.logger.bypass") || event.getPlayer().getGameMode() == GameMode.CREATIVE || fplayer.hasPvpProtection() || Board.getFactionAt(new FLocation(event.getPlayer())).noPvPInTerritory()) {
			HCFactions.getInstance().getCombatTagManager().removeCombatTagged(event.getPlayer());
			return;
		}
		// else spawn a logger every time, regardless of combat tagging
		HCFactions.getInstance().getCombatTagManager().addCombatTagged(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			LogoutTask task = HCFactions.getInstance().getLogoutTasks().get(player);
			if (task != null) {
				task.cancel();
				player.sendFormattedMessage("{0}Logout cancelled because you took damage.", ChatColor.RED);
			}
		}
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			LogoutTask task = HCFactions.getInstance().getLogoutTasks().get(damager);
			if (task != null) {
				task.cancel();
				damager.sendFormattedMessage("{0}Logout cancelled because you attacked.", ChatColor.RED);
			}
		}
	}

	@EventHandler
	public void onCombatTagCreate(CombatTagCreateEvent event) {
		long deathban = ((long) Conf.deathbanTime * 60 * 1000);
		FactionsLoggerNPC logger = new FactionsLoggerNPC(event.getPlayer(), deathban);
		event.setLoggerNPC(logger);
		logger.resetDespawnTimer();
	}

	@EventHandler
	public void onCombatTagDamage(CombatTagDamageEvent event) {
		EntityDamageByEntityEvent fakeEvent = new EntityDamageByEntityEvent(event.getDamager(), event.getLoggerNPC().getPlayer(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, event.getDamage());
		protectionListener.onEntityDamageByEntity(fakeEvent);
		entityListener.onEntityDamage(fakeEvent);
		entityListener.onEntityDamageByEntityMonitor(fakeEvent);
		if (fakeEvent.isCancelled()) {
			event.setCancelled(true);
		} else {
			((FactionsLoggerNPC) event.getLoggerNPC()).resetDespawnTimer();
		}
	}
}
