package cc.patrone.practice.listeners;

import zone.potion.utils.message.CC;
import zone.potion.utils.timer.Timer;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.match.MatchData;
import cc.patrone.practice.match.MatchState;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.tasks.PearlCooldownTask;
import cc.patrone.practice.utils.MathUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public class EntityListener implements Listener {
	private final PracticePlugin plugin;

	@EventHandler
	public void onShoot(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity().getShooter();

		if (player.getGameMode() != GameMode.SURVIVAL) {
			return;
		}

		PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());

		if (!profile.isInMatch()) {
			return;
		}

		if (event.getEntity() instanceof EnderPearl) {
			MatchData matchData = profile.getMatchData();
			Timer timer = matchData.getPearlTimer();

			if (!timer.isActive()) {
				player.setLevel(15);
				new PearlCooldownTask(player, profile).runTaskTimer(plugin, 2L, 2L);
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player entity = (Player) event.getEntity();
			PracticeProfile victimProfile = plugin.getPlayerManager().getProfile(entity.getUniqueId());

			if (victimProfile.getPlayerState() != PlayerState.FIGHTING) {
				event.setCancelled(true);
				return;
			}

			if (event.getDamager() instanceof Player) {
				PracticeProfile damagerProfile = plugin.getPlayerManager().getProfile(event.getDamager().getUniqueId());

				if (damagerProfile.getPlayerState() != PlayerState.FIGHTING) {
					event.setCancelled(true);
					return;
				}

				Match match = damagerProfile.getMatch();

				if (match == null) {
					return;
				}

				MatchData damagerMatchData = damagerProfile.getMatchData();
				MatchData victimMatchData = victimProfile.getMatchData();

				if (match.getMatchState() != MatchState.FIGHTING
						|| (damagerMatchData.getTeamId() == victimMatchData.getTeamId() && !match.isFfa())) {
					event.setCancelled(true);
					return;
				}

				if (match.getKit() == Kit.SUMO) {
					event.setDamage(0.0);
				}

				damagerMatchData.incrementHits();
				damagerMatchData.incrementCombo();
				victimMatchData.resetCombo();
			} else if (event.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) event.getDamager();

				if (arrow.getShooter() instanceof Player) {
					Player shooter = (Player) arrow.getShooter();

					if (entity == shooter) {
						return;
					}

					if (victimProfile.isInMatch()) {
						Match match = victimProfile.getMatch();

						PracticeProfile damagerProfile = plugin.getPlayerManager().getProfile(shooter.getUniqueId());

						MatchData damagerMatchData = damagerProfile.getMatchData();
						MatchData victimMatchData = victimProfile.getMatchData();

						if (match.getMatchState() != MatchState.FIGHTING
								|| (damagerMatchData.getTeamId() == victimMatchData.getTeamId() && !match.isFfa())) {
							event.setCancelled(true);
							return;
						}

						double health = MathUtil.roundToHalves(entity.getHealth() - event.getFinalDamage());

						if (health > 0.0) {
							shooter.sendMessage(CC.ACCENT + entity.getName() + CC.PRIMARY + " now has " + CC.PINK + health + "❤" + CC.PRIMARY + ".");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPotMiss(PotionSplashEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			for (PotionEffect effect : event.getEntity().getEffects()) {
				if (effect.getType().equals(PotionEffectType.HEAL)) {
					Player player = (Player) event.getEntity().getShooter();

					PracticeProfile profile = plugin.getPlayerManager().getProfile(player.getUniqueId());

					if (profile == null || !profile.isInMatch()) {
						return;
					}

					if (event.getIntensity(player) <= 0.5) {
						MatchData matchData = profile.getMatchData();

						matchData.incrementMissedPots();
					}
					return;
				}
			}
		}
	}
}
