package net.badlion.essentials.commands.warp;

import com.google.common.base.Joiner;
import net.badlion.essentials.EssentialsPlugin;
import net.badlion.essentials.managers.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (args.length == 0) {
                new BukkitRunnable() {
                    public void run() {
                        List<String> warps = WarpManager.getAllWarps();
                        if (warps.size() == 0) {
                            player.sendFormattedMessage("{0}No warps set", ChatColor.RED);
                        } else {
                            player.sendFormattedMessage("{0}All available warps: {1}", ChatColor.GOLD, Joiner.on(", ").join(warps));
                        }
                    }
                }.runTaskAsynchronously(EssentialsPlugin.getInstance());
            } else if (args.length == 1) {
                new BukkitRunnable() {
                    public void run() {
                        final Location location = WarpManager.getWarp(args[0]);
                        if (location == null) {
                            player.sendFormattedMessage("{0}Either no warp exists or it is not available right now (world not loaded)", ChatColor.RED);
                        } else {
                            new BukkitRunnable() {
                                public void run() {
                                    player.teleport(location);
                                    player.sendFormattedMessage("{0}Woosh", ChatColor.GOLD);
                                }
                            }.runTask(EssentialsPlugin.getInstance());
                        }
                    }
                }.runTaskAsynchronously(EssentialsPlugin.getInstance());
            } else {
                return false;
            }
        }

        return true;
    }

}
