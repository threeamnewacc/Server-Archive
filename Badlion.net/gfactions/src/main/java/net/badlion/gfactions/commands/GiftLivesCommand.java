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

public class GiftLivesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/giftlives [name] [# of lives]");
            return true;
        }

        int numOfLivesToGive = 0;
        try {
            numOfLivesToGive = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid usage.");
            return true;
        }

        if (numOfLivesToGive < 1) {
            sender.sendMessage(ChatColor.RED + "Invalid usage.");
            return true;
        }

        final int finalNumOfLivesToGive = numOfLivesToGive;
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            int numOfLives = DeathBanManager.getNumOfLives(player.getUniqueId());
            if (numOfLives <= 0) {
                player.sendMessage(ChatColor.RED + "You have no lives to gift :(");
            } else {
                if (finalNumOfLivesToGive > numOfLives) {
                    player.sendMessage(ChatColor.RED + "You do not have enough lives to gift. You only have " + ChatColor.YELLOW + numOfLives);
                    return true;
                }

                BukkitUtil.runTaskAsync(new Runnable() {
                    @Override
                    public void run() {
                        final UUID uuid = Gberry.getOfflineUUID(args[0]);
                        if (uuid == null) {
                            player.sendMessage(ChatColor.RED + "Could not find player " + args[0]);
                            return;
                        }

                        BukkitUtil.runTask(new Runnable() {
                            @Override
                            public void run() {
                                // No race conditions
                                int numOfLives = DeathBanManager.getNumOfLives(player.getUniqueId());
                                if (numOfLives <= 0) {
                                    player.sendMessage(ChatColor.RED + "You have no lives to gift :(");
                                    return;
                                } else if (finalNumOfLivesToGive > numOfLives) {
                                    player.sendMessage(ChatColor.RED + "You do not have enough lives to gift. You only have " + ChatColor.YELLOW + numOfLives);
                                    return;
                                }

                                // Do the swap
                                DeathBanManager.removeLives(player.getUniqueId(), finalNumOfLivesToGive);
                                DeathBanManager.addLives(uuid, finalNumOfLivesToGive);

                                player.sendMessage(ChatColor.DARK_GREEN + "You have given " + ChatColor.AQUA + args[0] + ChatColor.DARK_GREEN + " a total " + ChatColor.AQUA + args[1] + ChatColor.DARK_GREEN + " lives.");
                            }
                        });
                    }
                });
            }
        }

        return true;
    }

}
