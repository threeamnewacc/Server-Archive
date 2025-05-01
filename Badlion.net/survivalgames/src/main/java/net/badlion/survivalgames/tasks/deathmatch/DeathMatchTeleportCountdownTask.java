package net.badlion.survivalgames.tasks.deathmatch;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.tasks.CheckForEndGame;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import static net.badlion.mpg.MPGGame.GameState.POST_GAME;

public class DeathMatchTeleportCountdownTask extends BukkitRunnable {

    private int count = 0;

	private int teleportCountdownTime = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME);

    @Override
    public void run() {
	    if (CheckForEndGame.getInstance().isGameEnding() || MPG.getInstance().getMPGGame() == null || MPG.getInstance().getMPGGame().getGameState() == POST_GAME) {
		    this.cancel();
		    return;
	    }

	    if (this.count == this.teleportCountdownTime) {
		    MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.DEATH_MATCH_COUNTDOWN);
            this.cancel();
	        return;
        } else if (this.count >= 25 || this.count % 10 == 0) {
            Gberry.broadcastMessageNoBalance(MPGGame.DM_PREFIX + "Players will be teleported in " + ChatColor.GREEN
		            + (this.teleportCountdownTime - this.count) + ChatColor.GOLD + " seconds!");
		    Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "CLICK", "UI_BUTTON_CLICK"), 1F, 1F);
        }

        this.count++;
    }

}
