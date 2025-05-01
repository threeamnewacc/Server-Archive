package net.badlion.capturetheflag.tasks;

import net.badlion.capturetheflag.CTFTeam;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagRespawnTask extends BukkitRunnable {

	// TODO: MOVE TO MAP CONFIG
	private static final int FLAG_RESPAWN_THRESHOLD = 200; // 10 Seconds (Will depend on the map)

	private int ticks = 1;

	private final CTFTeam team;

    public FlagRespawnTask(CTFTeam team) {
        this.team = team;
    }

    @Override
    public void run() {
	    // Was the flag captured again by another team?
        if (this.team.isFlagTaken()) {
            this.cancel();
        }
        if (this.ticks == FlagRespawnTask.FLAG_RESPAWN_THRESHOLD) {
            team.removeFlag();
            team.setFlagLocation(team.getBaseFlagLocation());
            team.setFlagState(CTFTeam.FlagState.BASE);
            team.placeFlag();
            Gberry.broadcastMessage(MPG.MPG_PREFIX + team.getColor() + team.getTeamName() + " Team's " + ChatColor.GOLD + "flag was sent back to it's base!");
            this.cancel();
        }
        this.ticks++;
    }

}
