package com.massivecraft.factions.listeners;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ProtectionListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		if (HCFactions.getInstance().endOfTheWorld) {
			//Remove pvp prot due to eotw
			factionPlayer.setPvpProtection(0);
			return;
		}

		if (factionPlayer.hasPvpProtection()) {
			player.sendFormattedMessage("{0}You have {1}{2}{0} of pvp protection left.", CC.PRIMARY, CC.SECONDARY, HCFactions.getInstance().pvpProtectionManager.getProtectionTimeString(
					factionPlayer.getPlayer()));
			factionPlayer.startPvpProtectionCountdown();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();

		if (!player.isOnline()) {
			return;
		}
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		factionPlayer.setPvpProtection(Conf.pvpProtectionTime * 60 * 1000L);
		if (factionPlayer.hasPvpProtection()) {
			player.sendFormattedMessage("{0}You have {1}{2}{0} of pvp protection left.", CC.PRIMARY, CC.SECONDARY, HCFactions.getInstance().pvpProtectionManager.getProtectionTimeString(
					factionPlayer.getPlayer()));
			factionPlayer.startPvpProtectionCountdown();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() != EntityType.PLAYER) {
			return;
		}
		Player victim = (Player) event.getEntity();
		Entity potentialAttacker = event.getDamager();

		Player attacker;

		if (potentialAttacker instanceof Player) {
			attacker = (Player) potentialAttacker;
		} else if (potentialAttacker instanceof Projectile && ((Projectile) potentialAttacker).getShooter() instanceof Player) {
			attacker = (Player) ((Projectile) potentialAttacker).getShooter();
		} else {
			return;
		}
		FactionPlayer fAttacker = FPlayers.getInstance().get(attacker);
		FactionPlayer fVictim = FPlayers.getInstance().get(victim);

		if (fAttacker.hasPvpProtection()) {
			fAttacker.getPlayer().sendFormattedMessage("{0}You currently have PvP Protection on. Type ''/pvp enable'' to enable PvP.", CC.RED);
			event.setCancelled(true);
		} else if (fVictim.hasPvpProtection()) {
			fAttacker.getPlayer().sendFormattedMessage("{0}{1}{0} has PvP protection!", CC.RED, victim.getDisplayName());
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onHorseDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() != EntityType.HORSE) {
			return;
		}
		Horse victim = (Horse) event.getEntity();
		Entity potentialAttacker = event.getDamager();

		Player attacker;

		if (potentialAttacker instanceof Player) {
			attacker = (Player) potentialAttacker;
		} else if (potentialAttacker instanceof Projectile && ((Projectile) potentialAttacker).getShooter() instanceof Player) {
			attacker = (Player) ((Projectile) potentialAttacker).getShooter();
		} else {
			return;
		}
		FactionPlayer fAttacker = FPlayers.getInstance().get(attacker);


		if (victim.getPassenger() != null) {
			if (victim.getPassenger() instanceof Player) {
				Player rider = (Player) victim.getPassenger();
				FactionPlayer fRider = FPlayers.getInstance().get(rider);
				if (fAttacker.hasPvpProtection()) {
					fAttacker.getPlayer().sendFormattedMessage("{0}You currently have PvP Protection on. Type ''/pvp enable'' to enable PvP.", CC.RED);
					event.setCancelled(true);
				} else if (fRider.hasPvpProtection()) {
					fAttacker.getPlayer().sendFormattedMessage("{0}{1}{0} has PvP protection!", CC.RED, rider.getDisplayName());
					event.setCancelled(true);
				}
			}
		}

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		HCFactions.getInstance().pvpProtectionManager.addLootProtectArea(event.getEntity().getLocation());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		if (factionPlayer.hasPvpProtection()) {
			if (HCFactions.getInstance().pvpProtectionManager.isLocationLootProtected(event.getItem().getLocation())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!Conf.pvpProtectionHungerLoss) {
			Player player = (Player) event.getEntity();
			if (event.getFoodLevel() < player.getFoodLevel()) {
				if (FPlayers.getInstance().get(player).hasPvpProtection()) {
					player.setSaturation(20);
					player.setExhaustion(0);
					event.setCancelled(true);
				}
			}
		}
	}
}
