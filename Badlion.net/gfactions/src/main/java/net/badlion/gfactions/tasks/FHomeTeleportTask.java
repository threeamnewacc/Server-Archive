package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class FHomeTeleportTask extends BukkitRunnable {

    private Location location;
    private String hashCode;
    private UUID uuid;

    private int ticks = 0;
	private int lengthInTicks = 140;

	public FHomeTeleportTask(Location location, String hashCode, UUID uuid) {
		this.uuid = uuid;
		this.location = location;
		this.hashCode = hashCode;
	}

    public FHomeTeleportTask(Location location, String hashCode, UUID uuid, int lengthInTicks) {
	    this.uuid = uuid;
        this.location = location;
        this.hashCode = hashCode;

	    this.lengthInTicks = lengthInTicks;
    }

    @Override
    public void run() {
        final Player player = GFactions.plugin.getServer().getPlayer(this.uuid);

        // Only if they are still online
        if (player != null) {
            // Still in same spot
            Vector v1 = new Vector(this.location.getX(), this.location.getY(), this.location.getZ());
            Vector v2 = new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

            if (!GFactions.plugin.isInCombat(this.uuid)) {
                if (v1.distance(v2) < 2.0) {
                    if (this.ticks == this.lengthInTicks) {
                        // Store last location for /back
                        if (GFactions.plugin.getCmdSigns().getValidHashes().contains(this.hashCode)) {
                            GFactions.plugin.getCmdSigns().getValidHashes().remove(this.hashCode);

                            final Entity vehicle = player.getVehicle();
                            if (vehicle != null) {
                                vehicle.eject();

                                // TP the horse and player
                                GFactions.plugin.getServer().getScheduler().runTaskLater(GFactions.plugin, new Runnable() {

                                    @Override
                                    public void run() {
                                        player.performCommand("f home");
                                    }

                                }, 1); // 1 tick

                                // Reattach the player to the horse
                                GFactions.plugin.getServer().getScheduler().runTaskLater(GFactions.plugin, new Runnable() {

                                    @Override
                                    public void run() {
                                        if (vehicle instanceof Horse) {
                                            vehicle.teleport(player.getLocation());
                                        }
                                    }

                                }, 2); // 2 tick

                                // Reattach the player to the horse
                                GFactions.plugin.getServer().getScheduler().runTaskLater(GFactions.plugin, new Runnable() {

                                    @Override
                                    public void run() {
                                        if (vehicle instanceof Horse) {
                                            vehicle.setPassenger(player);
                                        }
                                    }

                                }, 3); // 3 tick
                            } else {
                                player.performCommand("f home");
                            }
                        }
                        this.cancel();
                    } else {
                        this.ticks = this.ticks + 5;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You moved in the grace period. The teleportation request has been cancelled.");
                    this.cancel();
                }
            } else {
                player.sendMessage(ChatColor.RED + "You have been combat tagged. The teleportation request has been cancelled.");
                this.cancel();
            }
        }
    }

}