package net.badlion.gfactions.tasks.parkour;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;

public class ParkourRemoveChestTask extends BukkitRunnable {

    private GFactions plugin;
    private boolean claimed;

    public ParkourRemoveChestTask(GFactions plugin, boolean claimed) {
        this.plugin = plugin;
        this.claimed = claimed;
    }

    @Override
    public void run() {
        if (this.plugin.getParkourChest() != null) {
            Block block = this.plugin.getParkourChest().getBlock();
            if (block.getType() == Material.CHEST) {
				// Clear chest, do not delete it
                Chest chest = (Chest) block.getState();
				chest.getBlockInventory().clear();

                this.plugin.setParkourChest(null);

                if (!claimed) {
                    Gberry.broadcastMessage(ChatColor.YELLOW + "The unclaimed parkour chest has been removed.");
                }
            }
        }
    }
}
