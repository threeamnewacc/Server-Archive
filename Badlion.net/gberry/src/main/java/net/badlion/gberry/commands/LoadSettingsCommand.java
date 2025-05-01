package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LoadSettingsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Gberry.getAllGlobalSettings();

        return true;
    }

}
