package net.badlion.practicechat;

import io.kohi.kpractice.PracticePlugin;
import io.kohi.kpractice.type.Party;
import net.badlion.smellychat.commands.handlers.ActiveCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AliasCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Party party = PracticePlugin.getInstance().getPartyManager().getParty(player);
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }

	        StringBuilder sb = new StringBuilder();
	        for (String s2 : args) {
		        sb.append(s2);
		        sb.append(" ");
	        }

	        String message = sb.toString();

	        // Pass in the real command name, not the alias
            ActiveCommandHandler.handleActiveCommand(player, command.getName().substring(0, 1).toUpperCase(), message);
        }
        return true;
    }

}
