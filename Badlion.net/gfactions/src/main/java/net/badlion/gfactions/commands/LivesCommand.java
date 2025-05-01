package net.badlion.gfactions.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.managers.DeathBanManager;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LivesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("add") && sender.hasPermission("GFactions.superadmin")) {
            BukkitUtil.runTaskAsync(new Runnable() {
                @Override
                public void run() {
                    UUID uuid = Gberry.getOfflineUUID(args[1]);
                    if (uuid == null) {
                        sender.sendMessage(ChatColor.RED + "Could not find " + args[1]);
                    }

                    DeathBanManager.addLives(uuid, Integer.parseInt(args[2]));
                    sender.sendMessage(ChatColor.GREEN + "Added lives.");
                }
            });

            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage(ChatColor.DARK_GREEN + "You currently have " + ChatColor.AQUA + DeathBanManager.getNumOfLives(player.getUniqueId()) + ChatColor.DARK_GREEN + " lives and " + ChatColor.AQUA + DeathBanManager.getHeartShards(player.getUniqueId()) + ChatColor.DARK_GREEN + " heart shards.");
            player.sendMessage(ChatColor.DARK_GREEN + "It takes a total of " + ChatColor.AQUA + DeathBanManager.getConfig().getHeartShardPieces() + ChatColor.DARK_GREEN + " heart shards to get a full life.");
        }

        return true;
    }

}
