package net.badlion.skywars.tasks.deathmatch;

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

public class DeathMatchStartCountdownTask extends BukkitRunnable {

	public static final int COUNTDOWN_TIME = 10;

	private int count = DeathMatchStartCountdownTask.COUNTDOWN_TIME * 20;

    private Map<UUID, Location> playerLocations;

    public DeathMatchStartCountdownTask(Map<UUID, Location> playerLocations) {
        this.playerLocations = playerLocations;
    }

    @Override
    public void run() {
	    // First check if there's only one player alive left, then check time
	    if (MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER).size() == 1) {
		    // Only one player left who killed everyone else, cancel task
		    this.cancel();
		    return;
	    } else if (this.count == 0) {
            MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.DEATH_MATCH);

            this.cancel();
            return;
        } else if (this.count % 20 == 0) {
            Gberry.broadcastMessageNoBalance(MPGGame.DM_PREFIX + "Will start in " + ChatColor.GREEN + (this.count / 20) + ChatColor.GOLD + " seconds!");
		    Gberry.broadcastSound(Sound.CLICK, 1F, 1F);
        }

	    // Run this every time to teleport players to starting point, essentially not letting them move
        ConcurrentLinkedQueue<MPGPlayer> mpgPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
        for (MPGPlayer mpgPlayer : mpgPlayers) {
            Player player = MPG.getInstance().getServer().getPlayer(mpgPlayer.getUniqueId());
            if (player != null) {
                Location prevLocation = this.playerLocations.get(player.getUniqueId());
                if (player.getLocation().getBlockX() != prevLocation.getBlockX()
		                || player.getLocation().getBlockZ() != prevLocation.getBlockZ()) {
                    Location newLocation = player.getLocation();
                    newLocation.setX(prevLocation.getX());
                    newLocation.setZ(prevLocation.getZ());

                    player.teleport(newLocation);
                }
            }
        }

        this.count--;
    }

}
