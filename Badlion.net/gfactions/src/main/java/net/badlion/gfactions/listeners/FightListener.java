package net.badlion.gfactions.listeners;

import net.badlion.gfactions.managers.BattleManager;
import net.badlion.gfactions.FightParticipant;
import net.badlion.gfactions.GFactions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashSet;
import java.util.Map;

/**
 * Got a lot of bases to cover in here...for now use LAST to ensure we get the last say it what is going on
 */
public class FightListener implements Listener {

	private GFactions plugin;
	private HashSet<PotionEffectType> debuffEffects = new HashSet<>();

	public FightListener(GFactions plugin) {
		this.plugin = plugin;
		this.debuffEffects.add(PotionEffectType.POISON);
		this.debuffEffects.add(PotionEffectType.SLOW);
		this.debuffEffects.add(PotionEffectType.HARM);
		this.debuffEffects.add(PotionEffectType.WEAKNESS);
	}

	private boolean isDebuff(PotionEffect potionEffect) {
		return this.debuffEffects.contains(potionEffect.getType()) || (potionEffect.getType() == PotionEffectType.HEALTH_BOOST && potionEffect.getAmplifier() == 2);
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void onPlayerShootArrow(ProjectileLaunchEvent event) {
		ProjectileSource source = event.getEntity().getShooter();
		if (source instanceof Player) {
			Player player = (Player) source;
			FightParticipant fightParticipant = BattleManager.playerToFightParticipantMap.get(player.getUniqueId().toString());
			if (fightParticipant != null) {
				fightParticipant.setArrowsShot(fightParticipant.getArrowsShot() + 1);
			}
		}
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void onPlayerThrowDebuff(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof ThrownPotion) {
			ProjectileSource source = event.getEntity().getShooter();
			if (source instanceof Player) {
				Player player = (Player) source;
				FightParticipant fightParticipant = BattleManager.playerToFightParticipantMap.get(player.getUniqueId().toString());
				if (fightParticipant != null) {
					ThrownPotion potion = (ThrownPotion) event.getEntity();
					for (PotionEffect potionEffect : potion.getEffects()) {
						if (this.isDebuff(potionEffect)) {
							// Track what they threw
							if (fightParticipant.getDebuffsThrown().containsKey(potionEffect.getType())) {
								fightParticipant.getDebuffsThrown().put(potionEffect.getType(), fightParticipant.getDebuffsThrown().get(potionEffect.getType()) + 1);
							} else {
								fightParticipant.getDebuffsThrown().put(potionEffect.getType(), 1);
							}

							return;
						}
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void onPlayerHitByDebuff(PotionSplashEvent event) {
		ProjectileSource source = event.getEntity().getShooter();
		if (source instanceof Player) {
			for (LivingEntity livingEntity : event.getAffectedEntities()) {
				if (livingEntity instanceof Player) {
					Player player = (Player) livingEntity;
					FightParticipant fightParticipant = BattleManager.playerToFightParticipantMap.get(player.getUniqueId().toString());
					if (fightParticipant != null) {
						ThrownPotion potion = event.getEntity();
						for (PotionEffect potionEffect : potion.getEffects()) {
							if (this.isDebuff(potionEffect)) {
								// Track what they got hit by
								if (fightParticipant.getDebuffsTaken().containsKey(potionEffect.getType())) {
									fightParticipant.getDebuffsTaken().put(potionEffect.getType(), fightParticipant.getDebuffsTaken().get(potionEffect.getType()) + 1);
								} else {
									fightParticipant.getDebuffsTaken().put(potionEffect.getType(), 1);
								}

								return;
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		long currentTime = System.currentTimeMillis();
		Player player = event.getEntity();
		FightParticipant fightParticipant = BattleManager.playerToFightParticipantMap.get(player.getUniqueId().toString());
		if (fightParticipant != null) {
			fightParticipant.setDeaths(fightParticipant.getDeaths() + 1);
			fightParticipant.setCurrentKillStreak(0);

			// Killer
			Player killer = player.getKiller();
			if (killer != null) {
				FightParticipant fightParticipantKiller = BattleManager.playerToFightParticipantMap.get(killer.getUniqueId().toString());
				if (fightParticipantKiller != null) {
					fightParticipantKiller.setKills(fightParticipantKiller.getKills() + 1);
					fightParticipantKiller.setCurrentKillStreak(fightParticipantKiller.getCurrentKillStreak() + 1);

					// Handle kill streak
					if (fightParticipantKiller.getCurrentKillStreak() > fightParticipantKiller.getMaxKillStreak()) {
						fightParticipantKiller.setMaxKillStreak(fightParticipantKiller.getCurrentKillStreak());
					}
				}
			}

			// Assists, within 60 seconds of player's life
			for (Map.Entry<String, Long> entry : fightParticipant.getMapOfLastHitTimeByPlayer().entrySet()) {
				if (entry.getValue() + 60000 > currentTime) {
					FightParticipant fightParticipantAssist = BattleManager.playerToFightParticipantMap.get(entry.getKey());
					if (fightParticipantAssist != null) {
						fightParticipantAssist.setAssists(fightParticipantAssist.getAssists());
					}
				}
			}

			// Handle final stuff in Life
			ItemStack killerItem = killer == null ? null : killer.getItemInHand();
			FightParticipant.Life life = new FightParticipant.Life(fightParticipant, currentTime, killerItem, event.getDrops());
			fightParticipant.getDeathInfo().add(life);

			// Update the last entry of the FightTime
			FightParticipant.FightTime fightTime = fightParticipant.getFightTimes().get(fightParticipant.getFightTimes().size() - 1);
			fightTime.setExitTime(currentTime);
		}
	}




}
