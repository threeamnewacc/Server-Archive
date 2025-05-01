package net.badlion.capturetheflag.tasks;


import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagPointsListenerTask extends BukkitRunnable {


    public void run() {
        for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
            CTFPlayer ctfPlayer = (CTFPlayer) mpgPlayer;
            if (ctfPlayer.isCarryingFlag()) {
                ctfPlayer.getTeam().addScore(1);
                ctfPlayer.addFlagHeldTime(2);
            }
        }
    }


}
