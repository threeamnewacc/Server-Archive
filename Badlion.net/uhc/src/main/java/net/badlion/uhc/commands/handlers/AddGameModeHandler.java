package net.badlion.uhc.commands.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddGameModeHandler {

    public static void handleGameMode(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (GameModeHandler.registerListener(sender, args[0])) {
                sender.sendMessage(ChatColor.GREEN + "Added game mode " + ChatColor.YELLOW + args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "This game mode is already being used or is not valid.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "/uhc addgamemode [name]");
        }
    }

}
