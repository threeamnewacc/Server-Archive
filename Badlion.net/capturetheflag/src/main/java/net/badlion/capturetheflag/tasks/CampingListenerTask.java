package net.badlion.capturetheflag.tasks;


import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CampingListenerTask extends BukkitRunnable {

    Map<CTFPlayer, Location> playerLocations;
    Map<CTFPlayer, Integer> timeCamping;

    public CampingListenerTask() {
        this.playerLocations = new HashMap<>();
        this.timeCamping = new HashMap<>();
    }

    public void run() {
        ConcurrentLinkedQueue<MPGPlayer> mpgPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
        Iterator it = mpgPlayers.iterator();
        while (it.hasNext()) {
            CTFPlayer ctfPlayer = (CTFPlayer) it.next();
            double x, z;
            if (playerLocations.get(ctfPlayer) != null) {
                x = ctfPlayer.getPlayer().getLocation().getX() - playerLocations.get(ctfPlayer).getX();
                z = ctfPlayer.getPlayer().getLocation().getZ() - playerLocations.get(ctfPlayer).getZ();
            } else {
                x = 0;
                z = 0;
            }

            double distanceSqaured = Math.pow(x, 2) + Math.pow(z, 2);

            if (distanceSqaured <=4 && timeCamping.get(ctfPlayer) != null) {
                timeCamping.put(ctfPlayer, timeCamping.get(ctfPlayer) + 1);
            } else {
                timeCamping.put(ctfPlayer, 0);
            }

            if (timeCamping.get(ctfPlayer) == 20) {
                ctfPlayer.getPlayer().kickPlayer("You have been kicked for camping!");
            } else if (timeCamping.get(ctfPlayer) >= 15) {
                ctfPlayer.getPlayer().sendMessage(MPG.MPG_PREFIX + ChatColor.DARK_RED + "Please stop camping! Move around or you will be kicked off the server!");
            }

            playerLocations.put(ctfPlayer, ctfPlayer.getPlayer().getLocation());

        }
    }

}
