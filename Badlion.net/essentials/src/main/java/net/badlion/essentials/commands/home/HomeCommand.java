package net.badlion.essentials.commands.home;

import com.google.common.base.Joiner;
import net.badlion.essentials.EssentialsPlugin;
import net.badlion.essentials.managers.HomeManager;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (args.length == 0) {
                new BukkitRunnable() {
                    public void run() {
                        List<String> homes = HomeManager.getAllHomes(player.getUniqueId());
                        if (homes.size() == 0) {
                            player.sendFormattedMessage("{0}You have no homes", ChatColor.RED);
                        } else {
                            player.sendFormattedMessage("{0}Your homes: {1}", ChatColor.GOLD, Joiner.on(", ").join(homes));
                        }
                    }
                }.runTaskAsynchronously(EssentialsPlugin.getInstance());
            } else if (args.length == 1) {
                new BukkitRunnable() {
                    public void run() {
                        final Location location = HomeManager.getHome(player.getUniqueId(), args[0]);
                        if (location == null) {
                            player.sendFormattedMessage("{0}Either no home exists or it is not available right now (world not loaded)", ChatColor.RED);
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
            } else if (args.length == 2 && player.hasPermission("badlion.essentials.mod")) {
                new BukkitRunnable() {
                    public void run() {
                        UUID uuid = Gberry.getOfflineUUID(args[0]);
                        if (uuid == null) {
                            player.sendFormattedMessage("{0}Player could not be found", ChatColor.RED);
                            return;
                        }

                        final Location location = HomeManager.getHome(uuid, args[1]);
                        if (location == null) {
                            player.sendFormattedMessage("{0}Either no home exists or it is not available right now (world not loaded)", ChatColor.RED);
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
