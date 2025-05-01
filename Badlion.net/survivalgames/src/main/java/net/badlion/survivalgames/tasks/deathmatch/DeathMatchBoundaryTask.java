package net.badlion.survivalgames.tasks.deathmatch;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeathMatchBoundaryTask extends BukkitRunnable {

    public static final int MAX_DISTANCE = SurvivalGames.getInstance().getSGGame().getWorld().getDeathMatchRadiusLimit();

	private Map<UUID, Location> playerLocations = new HashMap<>();

    @Override
    public void run() {
	    if (MPG.getInstance().getMPGGame() == null || MPG.getInstance().getMPGGame().getGameState() != MPGGame.GameState.DEATH_MATCH) {
		    this.cancel();
		    return;
	    }

        ConcurrentLinkedQueue<MPGPlayer> sgPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
        for (MPGPlayer sgPlayer : sgPlayers) {
            Player player = MPG.getInstance().getServer().getPlayer(sgPlayer.getUniqueId());
            if (player != null) {
                Location prevLocation = this.playerLocations.get(player.getUniqueId());
                if (player.getLocation().distance(SurvivalGames.getInstance().getSGGame().getWorld().getDeathMatchCenterLocation()) > DeathMatchBoundaryTask.MAX_DISTANCE) {
                    player.teleport(prevLocation);
                    player.sendMessage(ChatColor.RED + "Cannot leave the death match area!");
                } else {
                    this.playerLocations.put(player.getUniqueId(), player.getLocation());
                }
            }
        }
    }

}
