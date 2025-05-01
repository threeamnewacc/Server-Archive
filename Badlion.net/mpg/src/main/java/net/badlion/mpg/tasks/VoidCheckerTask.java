package net.badlion.mpg.tasks;

import net.badlion.gberry.utils.MessageUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.badlion.mpg.MPGGame.GameState.POST_GAME;

public class VoidCheckerTask extends BukkitRunnable {

    public void run() {
	    if (MPG.getInstance().getMPGGame() == null || MPG.getInstance().getMPGGame().getGameState() == POST_GAME) {
		    this.cancel();
		    return;
	    }

        for (Player player : Bukkit.getOnlinePlayers()) {
	        // Are they below the y level threshold?
	        if (player.getLocation().getY() < MPG.getInstance().getMPGGame().getWorld().getVoidDeathYLevel()) {
                MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());

		        if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			        // Set last damage to void
			        player.setLastDamageCause(MessageUtil.VOID_DAMAGE_EVENT);

			        // Damage player
			        player.damage(player.getMaxHealth());
		        } else {
			        player.setFallDistance(0);

			        player.teleport(MPG.getInstance().getMPGGame().getWorld().getSpectatorLocation());
		        }
	        }
        }
    }

}
