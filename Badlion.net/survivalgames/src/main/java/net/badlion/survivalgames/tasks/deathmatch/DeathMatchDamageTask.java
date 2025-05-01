package net.badlion.survivalgames.tasks.deathmatch;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.CheckForEndGame;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGSidebarManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeathMatchDamageTask extends BukkitRunnable {

	private static final double DAMAGE = 1D;
	private static final int DAMAGE_INTERVAL = 20; // In ticks
	public static int TIME_TO_START_DAMAGING = 180; // In seconds

	private int damageIntervalCounter = 0;

	private boolean pastTime = false;

	public DeathMatchDamageTask() {
		// Add the current time
		DeathMatchDamageTask.TIME_TO_START_DAMAGING += SurvivalGames.getInstance().getSGGame().getDeathmatchStartTime()
				+ DeathMatchStartCountdownTask.COUNTDOWN_TIME;
	}

	@Override
    public void run() {
		if (CheckForEndGame.getInstance().isGameEnding() || MPG.getInstance().getMPGGame() == null || MPG.getInstance().getMPGGame().getGameState() != MPGGame.GameState.DEATH_MATCH) {
			this.cancel();
			return;
		}

		if (this.pastTime) {
			this.damageIntervalCounter++;

			if (this.damageIntervalCounter == DeathMatchDamageTask.DAMAGE_INTERVAL) {
				this.damageIntervalCounter = 0;

				List<LivingEntity> alivePlayers = new ArrayList<>();

				// Add all online players
				for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
					alivePlayers.add(mpgPlayer.getPlayer());
				}

				// Add all offline players
				for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DC)) {
					alivePlayers.add(CombatTagPlugin.getInstance().getLogger(mpgPlayer.getUniqueId()).getEntity());
				}

				if (alivePlayers.size() <= 1) {
					this.cancel();
					return;
				}

				// Get a random player
				Collections.shuffle(alivePlayers);
				LivingEntity livingEntity = alivePlayers.get(0);

				livingEntity.damage(DeathMatchDamageTask.DAMAGE);

				// LivingEntity.damage() doesn't call EntityDamageEvent for some reason, so manually update sidebar health cache
				if (livingEntity instanceof Player) {
					SGSidebarManager.getPlayerHealthCache().put(livingEntity.getUniqueId(), livingEntity.getHealth());
				} else {
					SGSidebarManager.getPlayerHealthCache().put(CombatTagPlugin.getInstance().getCombatLoggerFromEntity(livingEntity).getUUID(), livingEntity.getHealth());
				}
			}
		} else if (GameTimeTask.getInstance().getTotalSeconds() >= DeathMatchDamageTask.TIME_TO_START_DAMAGING) {
			this.pastTime = true;
			Gberry.broadcastMessageNoBalance(MPGGame.DM_PREFIX + "Players will now randomly start taking damage");
		}
    }

}
