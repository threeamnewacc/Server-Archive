package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

public class ServerStatsCommand implements CommandExecutor {

    private Gberry plugin;

    public ServerStatsCommand(Gberry plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        sender.sendMessage(ChatColor.DARK_GREEN + "- Time Stats");
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Current server time: " + ChatColor.GOLD + new Date().toString());
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Startup time: " + ChatColor.GOLD + Gberry.startupTime.toString());

        Runtime runtime = Runtime.getRuntime();
        double mb = 1048576;
        DecimalFormat format = new DecimalFormat("###.##");

        sender.sendMessage(ChatColor.DARK_GREEN + "- Memory Stats");
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Max Memory: " + ChatColor.GOLD + (runtime.maxMemory() / mb) + ChatColor.DARK_GRAY + " MB");
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Total Memory: " + ChatColor.GOLD + Double.valueOf(format.format(runtime.totalMemory() / mb)) + ChatColor.DARK_GRAY + " MB (" + Double.valueOf(format.format(((double)runtime.totalMemory() / (double)runtime.maxMemory())*100)) + "%)");
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Free Memory: " + ChatColor.GOLD + Double.valueOf(format.format(runtime.freeMemory() / mb)) + ChatColor.DARK_GRAY + " MB (" + Double.valueOf(format.format(((double)runtime.freeMemory() / (double)runtime.maxMemory())*100)) + "%)");
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Used Memory: " + ChatColor.GOLD + Double.valueOf(format.format((runtime.totalMemory() - runtime.freeMemory()) / mb)) + ChatColor.DARK_GRAY + " MB");

        File log = new File(plugin.getDataFolder().getAbsoluteFile().getParentFile().getAbsoluteFile().getParent() + "/logs");

        sender.sendMessage(ChatColor.DARK_GREEN + "- Disk Stats");
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Server log size: " + ChatColor.GOLD + log.length() + " (" + Double.valueOf(format.format(log.length() / mb)) + " MB)");
        sender.sendMessage(ChatColor.DARK_GRAY +  "   Free log space: " + ChatColor.GOLD + log.getFreeSpace() + " (" + Double.valueOf(format.format(log.getFreeSpace() / mb)) + " MB)");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage(ChatColor.DARK_GREEN + "- World Stats");
            sender.sendMessage(ChatColor.DARK_GRAY +  "   Current world size: " + ChatColor.GOLD + (player.getWorld().getWorldFolder().length() / mb) + " MB");
            sender.sendMessage(ChatColor.DARK_GRAY +  "   Loaded chunks in this world: " + ChatColor.GOLD + player.getWorld().getLoadedChunks().length);
            sender.sendMessage(ChatColor.DARK_GRAY +  "   Living entities in this world: " + ChatColor.GOLD + player.getWorld().getLivingEntities().size());
            sender.sendMessage(ChatColor.DARK_GRAY +  "   Entities in this world: " + ChatColor.GOLD + player.getWorld().getEntities().size());
        }


        return true;
    }

}