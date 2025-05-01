package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WarpTask extends BukkitRunnable {

    private GFactions plugin;

    private Player player;
    private Location pLoc;
    private Location warpLoc;
    private boolean bypass;

    private int ticks = 0;

    public WarpTask(GFactions plugin, Player player, Location pLoc, Location warpLoc, boolean bypass) {
        this.plugin = plugin;

        this.player = player;
        this.pLoc = pLoc;
        this.warpLoc = warpLoc;
        this.bypass = bypass;
    }

    @Override
    public void run() {
        Vector v1 = new Vector(pLoc.getX(), pLoc.getY(), pLoc.getZ());
        Vector v2 = new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        if (!this.plugin.isInCombat(this.player.getUniqueId())) {
            if (v1.distance(v2) < 2.0) {
                if (ticks == 140 || bypass) {
                    final Entity vehicle = player.getVehicle();
                    if (vehicle != null) {
                        vehicle.eject();

                        // TP the horse and player
                        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {

                            @Override
                            public void run() {
                                if (vehicle instanceof Horse) {
                                    vehicle.teleport(warpLoc);
                                }
                                player.teleport(warpLoc);
                            }

                        }, 1); // 1 tick

                        // Reattach the player to the horse
                        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {

                            @Override
                            public void run() {
                                if (vehicle instanceof Horse) {
                                    vehicle.setPassenger(player);
                                }
                                player.sendMessage(ChatColor.GOLD + "Teleporting...");
                            }

                        }, 2); // 2 tick
                    } else {
                        player.teleport(warpLoc);
                        player.sendMessage(ChatColor.GOLD + "Teleporting...");
                    }
                    this.cancel();
                } else {
                    ticks = ticks + 5;
                }
            } else {
                player.sendMessage(ChatColor.RED + "You moved in the 7 second grace period. The teleportation request has been cancelled.");
                this.cancel();
            }
        } else {
            player.sendMessage(ChatColor.RED + "You have been combat tagged. The teleportation request has been cancelled.");
            this.cancel();
        }
    }

}