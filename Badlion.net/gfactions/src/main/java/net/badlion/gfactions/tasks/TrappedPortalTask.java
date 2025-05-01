package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TrappedPortalTask extends BukkitRunnable {

    private GFactions plugin;

    private Player player;

    private int secondsTrapped = 0;

    public TrappedPortalTask(GFactions plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.plugin.isInCombat(this.player)) {
            this.cancel();
        }

        if (this.player.getLocation().getBlock().getType().equals(Material.PORTAL)) {
			++this.secondsTrapped;
        } else {
            this.cancel();
        }

        if (this.secondsTrapped == 10) {
            this.player.teleport(this.plugin.getSpawnLocation());
            this.player.sendMessage(ChatColor.GOLD + "The server has detected a possible trapped portal, you have been teleported to spawn.");
            this.cancel();
        }
    }

}
