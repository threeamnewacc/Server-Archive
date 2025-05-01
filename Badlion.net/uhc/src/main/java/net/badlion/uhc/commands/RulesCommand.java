package net.badlion.uhc.commands;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.commands.handlers.RulesCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RulesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        if (BadlionUHC.getInstance().getConfigurator() == null) {
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.DARK_AQUA + "BadlionUHC" + ChatColor.GOLD + "" + ChatColor.BOLD + "] " + ChatColor.WHITE + "=============Rules=============");

        if (Gberry.serverName.startsWith("sa")) {
            for (String msg : RulesCommandHandler.spanishRules) {
                sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.DARK_AQUA + "BadlionUHC" + ChatColor.GOLD + "" + ChatColor.BOLD + "] " + ChatColor.WHITE + msg);
            }
        } else {
            for (String msg : RulesCommandHandler.rules) {
                sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.DARK_AQUA + "BadlionUHC" + ChatColor.GOLD + "" + ChatColor.BOLD + "] " + ChatColor.WHITE + msg);
            }
        }


        return true;
    }

}
