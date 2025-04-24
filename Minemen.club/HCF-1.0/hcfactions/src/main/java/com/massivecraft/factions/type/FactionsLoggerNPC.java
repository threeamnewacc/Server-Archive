package com.massivecraft.factions.type;

import club.minemen.hcfactions.HCFactions;
import club.minemen.hcfactions.combat.LoggerNPC;
import com.massivecraft.factions.listeners.DeathbanListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

public class FactionsLoggerNPC extends LoggerNPC {

	private static final int DESPAWN_TIME = 15; // seconds
	private static final FactionsEntityListener entityListener = new FactionsEntityListener();
	private static final ProtectionListener protectionListener = new ProtectionListener();
	private static final DeathbanListener deathbanListener = new DeathbanListener();
	private BukkitTask despawnTask;
	private long deathban;

	public FactionsLoggerNPC(Player player, long deathban) {
		super(player);
		this.deathban = deathban;
	}

	@Override
	public void remove(REMOVE_REASON reason) {
		super.remove(reason);

		// Don't do anything else, they didn't die
		if (reason == REMOVE_REASON.REJOIN) {
			return;
		}

		getPlayer().getInventory().clear();
		getPlayer().getInventory().setArmorContents(null);
		// set the craftplayer health to 0, which has the side effect of saving the player as dead in their data file
		// without actually killing the entity
		((CraftPlayer) getPlayer()).getHandle().setHealth(0);
		getPlayer().saveData();

		Player killer = this.getEntity().getKiller();

		if (killer != null) {
			Bukkit.broadcastMessage(ChatColor.RED + getEntity().getCustomName() + " (CombatLogger)" +
					ChatColor.YELLOW + " was slain by " + ChatColor.RED + killer.getName());
		} else {
			Bukkit.broadcastMessage(ChatColor.RED + getEntity().getCustomName() + " (CombatLogger)" + ChatColor.YELLOW + " has died");
		}

		// pass a death event to all our listeners
		PlayerDeathEvent event = new PlayerDeathEvent(getPlayer(), null, 0, null);
		entityListener.onPlayerDeath(event);
		protectionListener.onPlayerDeath(event);
		deathbanListener.onPlayerDeathLogger(event, deathban);
	}

	public void resetDespawnTimer() {
		if (despawnTask != null) {
			despawnTask.cancel();
		}
		despawnTask = Bukkit.getScheduler().runTaskLater(HCFactions.getInstance(), () -> remove(REMOVE_REASON.REJOIN), DESPAWN_TIME * 20);
	}
}
