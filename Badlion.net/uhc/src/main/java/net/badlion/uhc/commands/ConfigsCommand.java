package net.badlion.uhc.commands;

import net.badlion.uhc.commands.handlers.ConfigCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            ConfigCommandHandler.sendConfigsToPlayer(sender);
        }
        return true;
    }

}
