package net.badlion.capturetheflag.tasks;


import net.badlion.capturetheflag.CTFTeam;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagBaseRespawnTask extends BukkitRunnable {


    // TODO: MOVE TO MAP CONFIG
    private static final int FLAG_BASE_RESPAWN_THRESHOLD = 200; // 10 Seconds (Will depend on the map)

    private final CTFTeam ctfTeam;
    private int ticks = 1;

    public FlagBaseRespawnTask(CTFTeam ctfTeam) {
        this.ctfTeam = ctfTeam;
    }

    @Override
    public void run() {

        ticks++;

        if (this.ticks == FlagBaseRespawnTask.FLAG_BASE_RESPAWN_THRESHOLD) {
            ctfTeam.setFlagLocation(ctfTeam.getBaseFlagLocation());
            ctfTeam.placeFlag();
            ctfTeam.setFlagState(CTFTeam.FlagState.BASE);
            Gberry.broadcastMessage(MPG.MPG_PREFIX + ctfTeam.getColor() + ctfTeam.getTeamName() + " Team's " + ChatColor.GOLD + "flag was sent back to it's base!");
            this.cancel();
        }
    }
}
