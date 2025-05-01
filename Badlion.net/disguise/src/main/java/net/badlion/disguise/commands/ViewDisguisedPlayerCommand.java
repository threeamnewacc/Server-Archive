package net.badlion.disguise.commands;

import net.badlion.disguise.DisguisedPlayer;
import net.badlion.disguise.managers.DisguiseManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ViewDisguisedPlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) {
            return false;
        }

        UUID uuid = DisguiseManager.getDisguisedUUID(args[0]);
        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or not disguised.");
        } else {
            DisguisedPlayer disguisedPlayer = DisguiseManager.getDisguisePlayer(uuid);

            sender.sendMessage(ChatColor.GREEN + disguisedPlayer.getDisguisedName() + ChatColor.YELLOW
		            + "'s disguised name is " + ChatColor.GREEN + disguisedPlayer.getUsername());
        }

        return true;
    }

}
