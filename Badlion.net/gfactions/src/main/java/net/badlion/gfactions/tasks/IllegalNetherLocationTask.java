package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IllegalNetherLocationTask extends BukkitRunnable {

    private GFactions plugin;

    public IllegalNetherLocationTask(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : this.plugin.getServer().getOnlinePlayers()) {
            if (p.getWorld().getName().equals("world_nether") && p.getLocation().getY() >= 128) {
                p.teleport(this.plugin.getSpawnLocation()); // back 2 lumby u go
				p.sendMessage(ChatColor.RED + "You are not allowed to be above the nether.");
            }
        }
    }

}
