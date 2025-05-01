package net.badlion.essentials.commands.warp;

import net.badlion.essentials.EssentialsPlugin;
import net.badlion.essentials.managers.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SetWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (args.length != 1) {
                return false;
            } else {
                new BukkitRunnable() {
                    public void run() {
                        WarpManager.updateWarp(args[0], player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Warp added/updated");
                    }
                }.runTaskAsynchronously(EssentialsPlugin.getInstance());
            }
        }

        return true;
    }

}
