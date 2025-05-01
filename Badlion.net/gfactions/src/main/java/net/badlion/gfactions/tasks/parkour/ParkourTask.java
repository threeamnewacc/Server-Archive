package net.badlion.gfactions.tasks.parkour;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ParkourTask extends BukkitRunnable {

    private GFactions plugin;

    public ParkourTask(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (this.plugin.getParkourChest() == null) {
            this.cancel();
            return;
        }

        for (ItemStack stack : this.plugin.getParkourChest().getBlockInventory().getContents()) {
            if (stack != null) {
                // Items are still in the chest
                return;
            }
        }

        this.cancel();

		Gberry.broadcastMessage(ChatColor.YELLOW + "The parkour chest has been claimed.");

        // Remove chest
        new ParkourRemoveChestTask(this.plugin, true).runTask(this.plugin);

        // Call TabList event
        EventStateChangeEvent event = new EventStateChangeEvent("Parkour", false);
        this.plugin.getServer().getPluginManager().callEvent(event);
    }
}
