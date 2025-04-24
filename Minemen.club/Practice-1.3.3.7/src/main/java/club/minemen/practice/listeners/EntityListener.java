package club.minemen.practice.listeners;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchState;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityListener implements Listener {
	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

			switch (playerData.getPlayerState()) {
				case FIGHTING:
					Match match = this.plugin.getMatchManager().getMatch(playerData);
					if (match.getMatchState() != MatchState.FIGHTING) {
						e.setCancelled(true);
					}
					if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
						this.plugin.getMatchManager().removeFighter(player, playerData, true);
					}
					break;
				default:
					if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
						e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
					}
					e.setCancelled(true);
					break;
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		Player entity = (Player) e.getEntity();

		Player damager;

		if (e.getDamager() instanceof Player) {
			damager = (Player) e.getDamager();
		} else if (e.getDamager() instanceof Projectile) {
			damager = (Player) ((Projectile) e.getDamager()).getShooter();
		} else {
			return;
		}

		PlayerData entityData = this.plugin.getPlayerManager().getPlayerData(entity.getUniqueId());
		PlayerData damagerData = this.plugin.getPlayerManager().getPlayerData(damager.getUniqueId());

		if (damagerData.getPlayerState() != PlayerState.FIGHTING ||
				entityData.getPlayerState() != PlayerState.FIGHTING) {
			e.setCancelled(true);
			return;
		}

		Match match = this.plugin.getMatchManager().getMatch(entityData);

		if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
			e.setCancelled(true);
			return;
		}

		if (match.getKit().isSpleef() || match.getKit().isSumo()) {
			e.setDamage(0.0D);
		}

		if (e.getDamager() instanceof Player) {
			damagerData.setCombo(damagerData.getCombo() + 1);
			damagerData.setHits(damagerData.getHits() + 1);
			if (damagerData.getCombo() > damagerData.getLongestCombo()) {
				damagerData.setLongestCombo(damagerData.getCombo());
			}
			entityData.setCombo(0);
			if (match.getKit().isSpleef()) {
				e.setCancelled(true);
			}
		} else if (e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getDamager();

			if (arrow.getShooter() instanceof Player) {
				Player shooter = (Player) arrow.getShooter();

				if (!entity.getName().equals(shooter.getName())) {
					double health = Math.ceil(entity.getHealth() - e.getFinalDamage()) / 2.0D;

					if (health > 0.0D) {
						shooter.sendMessage(CC.SECONDARY + entity.getName() + CC.PRIMARY + " is now at "
								+ CC.SECONDARY + health + "❤" + CC.PRIMARY + ".");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		for (PotionEffect effect : e.getEntity().getEffects()) {
			if (effect.getType().equals(PotionEffectType.HEAL)) {
				Player shooter = (Player) e.getEntity().getShooter();

				if (e.getIntensity(shooter) <= 0.5D) {
					PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());

					if (shooterData != null) {
						shooterData.setMissedPots(shooterData.getMissedPots() + 1);
					}
				}
				break;
			}
		}
	}
}
