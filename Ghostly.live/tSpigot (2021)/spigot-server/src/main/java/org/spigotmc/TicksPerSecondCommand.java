package org.spigotmc;

import java.lang.management.ManagementFactory;

import me.enzol.spigot.util.DateUtil;
import net.minecraft.server.DedicatedServer;
import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public class TicksPerSecondCommand extends Command {

    public TicksPerSecondCommand(String name) {
        super(name);

        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    private static String format(double tps) {
        return (tps >= 18.0 ? ChatColor.GREEN : tps >= 15.0 ? ChatColor.YELLOW : ChatColor.RED).toString() + (tps > 20.0 ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, DedicatedServer.TPS);
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        final double[] tps = org.bukkit.Bukkit.spigot().getTPS();
        String[] tpsAvg = new String[tps.length];

        for (int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format(tps[i]);
        }

        int totalEntities = 0;
        int livingEntities = 0;

        for (World world : Bukkit.getServer().getWorlds()) {
            totalEntities += world.getEntities().size();

            for (org.bukkit.entity.Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity) {
                    livingEntities++;
                }
            }
        }

        final long usedMemory = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2) / 1048576L;
        final long allocatedMemory = Runtime.getRuntime().totalMemory() / 1048576L;

        sender.sendMessage("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "----------------------------------------------");
        sender.sendMessage("" + ChatColor.GOLD + "Server Information");
        sender.sendMessage(" " + ChatColor.GOLD + "TPS (1m, 5m, 15m): " + org.apache.commons.lang.StringUtils.join(tpsAvg, ", "));
        sender.sendMessage(" " + ChatColor.GOLD + "Online Players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        sender.sendMessage(" " + ChatColor.GOLD + "Entities: " + ChatColor.GREEN + "(Total: " + totalEntities + ") (Living: " + livingEntities + ")");
        sender.sendMessage(" " + ChatColor.GOLD + "Last Tick: " + ChatColor.GREEN + (System.currentTimeMillis() - MinecraftServer.LAST_TICK_TIME) + "ms");
        sender.sendMessage("");
        sender.sendMessage(" " + ChatColor.GOLD + "Memory: " + ChatColor.GREEN + "(Used: " + usedMemory + "mb) (Allocated: " + allocatedMemory + "mb)");
        sender.sendMessage(" " + ChatColor.GOLD + "Uptime: " + ChatColor.GREEN + DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        sender.sendMessage("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "----------------------------------------------");

        return true;
    }

}