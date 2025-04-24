package com.massivecraft.factions.listeners;

import club.minemen.hcfactions.HCFactions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.DeathbanEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DeathbanListener implements Listener {

	private final Cache<UUID, Long> lastLoginAttempt = CacheBuilder.newBuilder().expireAfterWrite(6000, TimeUnit.MILLISECONDS).build();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		if (HCFactions.getInstance().getDeathbanManager().isDeathBanned(event.getUniqueId())) {
			int totalLives = HCFactions.getInstance().getDeathbanManager().getLives(event.getUniqueId());
			if (totalLives > 0) {
				Long time = this.lastLoginAttempt.getIfPresent(event.getUniqueId());
				if (time != null) {
					HCFactions.getInstance().getDeathbanManager().removeLives(event.getUniqueId(), 1);
					HCFactions.getInstance().getDeathbanManager().unDeathbanPlayer(event.getUniqueId());
					HCFactions.getInstance().getLogger().info(event.getName() + " has revived themselves with a life!");
					return;
				}
			}

			this.lastLoginAttempt.put(event.getUniqueId(), System.currentTimeMillis());

			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
					HCFactions.getInstance().getDeathbanManager().getDeathbanMessage(event.getUniqueId(), totalLives > 0, totalLives));
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		DeathbanEvent deathbanEvent = new DeathbanEvent(player);

		deathbanEvent.setDeathbanTime(((long) Conf.deathbanTime * 60 * 1000));
		HCFactions.getInstance().getServer().getPluginManager().callEvent(deathbanEvent);

		if (!deathbanEvent.isCancelled() && !player.hasPermission("factions.deathban.bypass")) {
			player.getWorld().strikeLightningEffect(player.getLocation());

			HCFactions.getInstance().getDeathbanManager().deathbanPlayer(player.getUniqueId(), deathbanEvent.getDeathbanTime());

			new BukkitRunnable() {
				@Override
				public void run() {
					Player p = Bukkit.getPlayer(player.getUniqueId());
					if (p != null) {
						p.kickPlayer(HCFactions.getInstance().getDeathbanManager().getDeathbanMessage(p.getUniqueId(), HCFactions.getInstance().getDeathbanManager().getLives(p.getUniqueId()) > 0, HCFactions.getInstance().getDeathbanManager().getLives(p.getUniqueId())));
					}
				}
			}.runTaskLater(HCFactions.getInstance(), 200);
		}
	}

	public void onPlayerDeathLogger(PlayerDeathEvent event, long deathban) {
		Player player = event.getEntity();
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		DeathbanEvent deathbanEvent = new DeathbanEvent(player);

		deathbanEvent.setDeathbanTime(deathban);
		HCFactions.getInstance().getServer().getPluginManager().callEvent(deathbanEvent);

		if (!deathbanEvent.isCancelled() && !player.hasPermission("factions.deathban.bypass")) {
			player.getWorld().strikeLightningEffect(player.getLocation());

			HCFactions.getInstance().getDeathbanManager().deathbanPlayer(player.getUniqueId(), deathbanEvent.getDeathbanTime());

			new BukkitRunnable() {
				@Override
				public void run() {
					Player p = Bukkit.getPlayer(player.getUniqueId());
					if (p != null) {
						p.kickPlayer(HCFactions.getInstance().getDeathbanManager().getDeathbanMessage(p.getUniqueId(), HCFactions.getInstance().getDeathbanManager().getLives(p.getUniqueId()) > 0, HCFactions.getInstance().getDeathbanManager().getLives(p.getUniqueId())));
					}
				}
			}.runTaskLater(HCFactions.getInstance(), 200);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (HCFactions.getInstance().getDeathbanManager().isDeathBanned(player.getUniqueId())) {
			player.kickPlayer(HCFactions.getInstance().getDeathbanManager().getDeathbanMessage(player.getUniqueId(), HCFactions.getInstance().getDeathbanManager().getLives(player.getUniqueId()) > 0, HCFactions.getInstance().getDeathbanManager().getLives(player.getUniqueId())));
		}
	}
}
