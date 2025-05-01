package net.badlion.uhc.tasks;

import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HealTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) { // Don't have to do Bukkit.getPlayerExact like we would with players map
            p.setHealth(p.getMaxHealth());
            p.sendMessage(ChatColor.GREEN + "You have been healed!");

            UHCPlayerManager.updateHealthScores(p);
        }
    }
}
