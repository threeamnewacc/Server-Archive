package net.badlion.essentials.commands.home;

import net.badlion.essentials.EssentialsPlugin;
import net.badlion.essentials.managers.HomeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (args.length != 1) {
                return false;
            } else {
                new BukkitRunnable() {
                    public void run() {
                        HomeManager.updateHome(player.getUniqueId(), args[0], player.getLocation());
                        player.sendFormattedMessage("{0}Home added/updated", ChatColor.GREEN);
                    }
                }.runTaskAsynchronously(EssentialsPlugin.getInstance());
            }
        }

        return true;
    }

}
