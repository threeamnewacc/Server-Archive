package net.badlion.skywars.tasks.deathmatch;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.skywars.SkyWars;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeathMatchDamageTask extends BukkitRunnable {

	private static final double DAMAGE = 1.5D;
	private static final int DAMAGE_INTERVAL = 20; // In ticks
	public static int TIME_TO_START_DAMAGING = 60; // In seconds

	private int damageIntervalCounter = 0;

	private boolean pastTime = false;

	public DeathMatchDamageTask() {
		// Add the current time
		DeathMatchDamageTask.TIME_TO_START_DAMAGING += SkyWars.getInstance().getCurrentGame().getDeathmatchStartTime()
				+ DeathMatchStartCountdownTask.COUNTDOWN_TIME;
	}

	public static void start(int tickInterval) {
		new DeathMatchDamageTask().runTaskTimer(MPG.getInstance(), 0, tickInterval);
	}

	@Override
    public void run() {
		if (MPG.getInstance().getMPGGame().getGameState() != MPGGame.GameState.DEATH_MATCH) {
			this.cancel();
			return;
		}

		if (this.pastTime) {
			this.damageIntervalCounter++;

			if (this.damageIntervalCounter == DeathMatchDamageTask.DAMAGE_INTERVAL) {
				this.damageIntervalCounter = 0;

				List<Player> alivePlayers = new ArrayList<>();
				for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
					Player player = MPG.getInstance().getServer().getPlayer(mpgPlayer.getUniqueId());
					if (player != null) {
						alivePlayers.add(player);
					} else {
						mpgPlayer.handlePlayerDeathInternal();
					}
				}

				if (alivePlayers.size() <= 1) {
					this.cancel();
					return;
				}

				// Get a random player
				Collections.shuffle(alivePlayers);
				Player player = alivePlayers.get(0);

				player.damage(DeathMatchDamageTask.DAMAGE);

				// Set damage manually because entity damage event won't get called for player.damage()
				MPGPlayerManager.getMPGPlayer(player).setLastDamageCause(EntityDamageEvent.DamageCause.CONTACT);
			}
		} else if (GameTimeTask.getInstance().getTotalSeconds() >= DeathMatchDamageTask.TIME_TO_START_DAMAGING) {
			this.pastTime = true;
			Gberry.broadcastMessageNoBalance(MPGGame.DM_PREFIX + "Players will now randomly start taking damage");
		}
    }

}
