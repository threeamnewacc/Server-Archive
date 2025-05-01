package net.badlion.uhc.commands.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveGameModeHandler {

    public static void handleGameMode(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (GameModeHandler.unregisterListener(args[0])) {
                sender.sendMessage(ChatColor.GREEN + "Removed game mode " + ChatColor.YELLOW + args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "This game mode is not valid.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "/uhc removegamemode [name]");
        }
    }

}
