package net.badlion.uhc.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TrappedPortalTask extends BukkitRunnable {

    private Player player;
    private Location location;

    private int secondsTrapped = 0;

    public TrappedPortalTask(Location location, Player player) {
        this.location = location;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.player.getLocation().getBlock().getType().equals(Material.PORTAL)) {
			++this.secondsTrapped;
        } else {
            this.cancel();
        }

        if (this.secondsTrapped == 10) {
            this.player.teleport(this.location);
            this.player.sendMessage(ChatColor.GOLD + "The server has detected a possible trapped portal, you have been teleported to your original location.");
            this.cancel();
        }
    }

}
