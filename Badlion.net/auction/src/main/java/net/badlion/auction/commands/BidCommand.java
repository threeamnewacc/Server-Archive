package net.badlion.auction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BidCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            if (strings.length != 1) {
                return false;
            }

            ((Player) sender).performCommand("auction bid " + strings[0]);

        } else {
            sender.sendMessage("You can only use this command in-game!");
        }
        return true;
    }

}
