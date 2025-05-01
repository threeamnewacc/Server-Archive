package net.badlion.gberry.commands;

import net.badlion.gberry.GMapManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GetMapCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        if (args[0].equalsIgnoreCase("value")) {
            if (args.length < 3) {
                return false;
            }

            UUID uuid;
            if (args[2].length() <= 16) {
                Player p = Bukkit.getPlayerExact(args[2]);
                if (p == null) {
                    sender.sendMessage(ChatColor.RED + "No player found");
                    return true;
                }

                uuid = p.getUniqueId();
            } else {
                try {
                    uuid = UUID.fromString(args[2]);
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(ChatColor.RED + "Invalid UUID");
                    return true;
                }
            }

            String value = GMapManager.getInstance().getValue(args[1], uuid);
            sender.sendMessage(value);
            Bukkit.getLogger().info(value);
        } else {
            List<String> results;

            if (args[0].equalsIgnoreCase("keyvalues")) {
                results = GMapManager.getInstance().getAllKeyValues(args[1]);
            } else if (args[0].equalsIgnoreCase("values")) {
                results = GMapManager.getInstance().getAllValues(args[1]);
            } else if (args[0].equalsIgnoreCase("keys")) {
                results = GMapManager.getInstance().getAllKeys(args[1]);
            } else {
                return false;
            }

            // Log and send info
            for (String s : results) {
                sender.sendMessage(s);
                Bukkit.getLogger().info(s);
            }
        }

        return true;
    }

}
