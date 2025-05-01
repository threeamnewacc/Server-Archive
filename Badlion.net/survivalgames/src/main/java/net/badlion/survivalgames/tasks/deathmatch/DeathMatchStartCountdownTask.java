package net.badlion.survivalgames.tasks.deathmatch;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.CheckForEndGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

import static net.badlion.mpg.MPGGame.GameState.POST_GAME;

public class DeathMatchStartCountdownTask extends BukkitRunnable {

	public static final int COUNTDOWN_TIME = 10;

	private int count = DeathMatchStartCountdownTask.COUNTDOWN_TIME * 20;

    private Map<UUID, Location> playerLocations;

    public DeathMatchStartCountdownTask(Map<UUID, Location> playerLocations) {
        this.playerLocations = playerLocations;
    }

    @Override
    public void run() {
	    if (CheckForEndGame.getInstance().isGameEnding() || MPG.getInstance().getMPGGame() == null || MPG.getInstance().getMPGGame().getGameState() == POST_GAME) {
		    this.cancel();
		    return;
	    }

	    if (this.count == 0) {
            MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.DEATH_MATCH);

            this.cancel();
            return;
        } else if (this.count % 20 == 0) {
            Gberry.broadcastMessageNoBalance(MPGGame.DM_PREFIX + "Will start in " + ChatColor.GREEN + (this.count / 20) + ChatColor.GOLD + " seconds!");
		    Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "CLICK", "UI_BUTTON_CLICK"), 1F, 1F);
        }

	    // Run this every time to teleport players to starting point, essentially not letting them move
        for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
            Player player = mpgPlayer.getPlayer();

	        Location teleportLocation = this.playerLocations.get(mpgPlayer.getUniqueId());

	        if (player.getLocation().getBlockX() != teleportLocation.getBlockX()
			        || player.getLocation().getBlockZ() != teleportLocation.getBlockZ()) {
		        Location newLocation = player.getLocation();
		        newLocation.setX(teleportLocation.getX());
		        newLocation.setZ(teleportLocation.getZ());

		        player.teleport(newLocation);
	        }
        }

	    // Run this every time to teleport players to starting point, essentially not letting them move
	    for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DC)) {
		    LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(mpgPlayer.getUniqueId());

		    Location teleportLocation = this.playerLocations.get(mpgPlayer.getUniqueId());
		    if (loggerNPC.getEntity().getLocation().getBlockX() != teleportLocation.getBlockX()
				    || loggerNPC.getEntity().getLocation().getBlockZ() != teleportLocation.getBlockZ()) {
			    Location newLocation = loggerNPC.getEntity().getLocation();
			    newLocation.setX(teleportLocation.getX());
			    newLocation.setZ(teleportLocation.getZ());

			    loggerNPC.getEntity().teleport(newLocation);
		    }
	    }

        this.count--;
    }

}
