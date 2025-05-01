package net.badlion.mpg.tasks;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PreGameCountdownTask extends BukkitRunnable {

	private static PreGameCountdownTask instance;

	private Map<UUID, Location> playerLocations;

	private int counter = 20 * MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.START_COUNTDOWN_SECONDS);

	public PreGameCountdownTask(Map<UUID, Location> playerLocations) {
		PreGameCountdownTask.instance = this;

		this.playerLocations = playerLocations;
	}

    @Override
    public void run() {
	    int seconds = this.counter / 20;

        if (this.counter % 20 == 0 && seconds == 0) {
	        MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.GAME);

            this.cancel();
            return;
        }

	    if (this.counter % 20 == 0 && (seconds % 5 == 0 || seconds <= 10)) {
		    Gberry.broadcastMessage(MPG.MPG_PREFIX + ChatColor.GOLD + "Starting in " + ChatColor.AQUA + seconds + ChatColor.GOLD + " seconds!");
		    Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "CLICK", "UI_BUTTON_CLICK"), 1F, 1F);
	    }

	    // Teleport players to their spawn points if this game has them
	    if (this.playerLocations != null) {
		    // Run this every time to teleport players to starting point, essentially not letting them move
		    ConcurrentLinkedQueue<MPGPlayer> players = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
		    for (MPGPlayer mpgPlayer : players) {
			    Player player = MPG.getInstance().getServer().getPlayer(mpgPlayer.getUniqueId());

			    // Null check because if player logs off pregame we don't set them to DC state yet
			    if (player != null) {
				    Location prevLocation = this.playerLocations.get(player.getUniqueId());

				    // Could be null somehow?
				    if (prevLocation != null) {
					    if (player.getLocation().getBlockX() != prevLocation.getBlockX()
							    || player.getLocation().getBlockZ() != prevLocation.getBlockZ()) {
						    Location newLocation = player.getLocation();
						    newLocation.setX(prevLocation.getX());
						    newLocation.setZ(prevLocation.getZ());

						    player.teleport(newLocation);
					    }
				    } else {
					    this.playerLocations.put(player.getUniqueId(), player.getLocation());
				    }
			    }
		    }
	    }

        this.counter--;
    }

	public static PreGameCountdownTask getInstance() {
		return PreGameCountdownTask.instance;
	}

	public Location getPlayerSpawnLocation(UUID uuid) {
		return this.playerLocations.get(uuid);
	}

}
