package net.badlion.essentials.commands.home;

import com.google.common.base.Joiner;
import net.badlion.essentials.EssentialsPlugin;
import net.badlion.essentials.managers.HomeManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DeleteHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (args.length != 1) {
                return false;
            } else {
                new BukkitRunnable() {
                    public void run() {
                        if (HomeManager.doesHomeExist(player.getUniqueId(), args[0])) {
                            HomeManager.deleteHome(player.getUniqueId(), args[0]);
                            player.sendFormattedMessage("{0}Home has been deleted", ChatColor.GREEN);
                        } else {
                            player.sendFormattedMessage("{0}No home exists", ChatColor.RED);
                        }
                    }
                }.runTaskAsynchronously(EssentialsPlugin.getInstance());
            }
        }

        return true;
    }

}
