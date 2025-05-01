package net.badlion.build.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartTask extends BukkitRunnable {

    private int timer = 0;

    @Override
    public void run() {
        ++timer;

        if (this.timer >= 360) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        } else if (this.timer == 358) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say Server rebooting in 2 minutes");
        } else if (this.timer == 359) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say Server rebooting in 1 minute");
        }
    }

}
