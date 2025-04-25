package cc.patrone.practice.task;

import cc.patrone.practice.PracticePlugin;
import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class InventoryTask extends BukkitRunnable {

    private PracticePlugin plugin;

    public void run() {
        this.plugin.getManagerHandler().getInventoryManager().updateUnrankedInventory();
        this.plugin.getManagerHandler().getInventoryManager().updateRankedInventory();
    }

}
