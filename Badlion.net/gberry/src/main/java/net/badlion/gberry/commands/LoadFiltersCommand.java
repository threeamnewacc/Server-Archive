package net.badlion.gberry.commands;

import net.badlion.gberry.listeners.ChatListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LoadFiltersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        ChatListener.loadChatFilters();

        sender.sendMessage("Loaded");

        return true;
    }

}
