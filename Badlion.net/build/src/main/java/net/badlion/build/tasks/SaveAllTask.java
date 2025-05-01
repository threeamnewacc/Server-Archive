package net.badlion.build.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveAllTask extends BukkitRunnable {

    public void run() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
    }

}
