package net.badlion.essentials.commands.home;

import com.google.common.base.Joiner;
import net.badlion.essentials.EssentialsPlugin;
import net.badlion.essentials.managers.HomeManager;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class ListHomesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (args.length == 1) {
                new BukkitRunnable() {
                    public void run() {
                        UUID uuid = Gberry.getOfflineUUID(args[0]);
                        if (uuid == null) {
                            player.sendFormattedMessage("{0}Player could not be found", ChatColor.RED);
                            return;
                        }

                        List<String> homes = HomeManager.getAllHomes(uuid);
                        if (homes.size() == 0) {
                            player.sendFormattedMessage("{0}This player has no homes", ChatColor.RED);
                        } else {
                            player.sendFormattedMessage("{0}''s homes: {1}", ChatColor.GOLD + args[0], Joiner.on(", ").join(homes));
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
