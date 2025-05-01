package net.badlion.uhc.tasks;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.PVPProtectionTurnedOnEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class PvPTimerTask extends BukkitRunnable {

    @Override
    public void run() {
        BadlionUHC.getInstance().setPVP(true);
        Bukkit.broadcastMessage(ChatColor.AQUA + "PvP is now enabled!");

        Bukkit.getPluginManager().callEvent(new PVPProtectionTurnedOnEvent());
    }
}
