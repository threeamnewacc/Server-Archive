package net.badlion.potionchat;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
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
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (!group.isParty()) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }

	        StringBuilder sb = new StringBuilder();
	        for (String s2 : args) {
		        sb.append(s2);
		        sb.append(" ");
	        }

	        String message = sb.toString();

            ActiveCommandHandler.handleActiveCommand(player, s.substring(0, 1).toUpperCase(), message);
        }
        return true;
    }

}
