package net.badlion.uhcchat;

import net.badlion.smellychat.commands.handlers.ActiveCommandHandler;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AliasCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Are they in a team?
            if (BadlionUHC.getInstance().getGameType() != UHCTeam.GameType.TEAM) {
                player.sendMessage(ChatColor.RED + "You are not in a team!");
                return true;
            }

	        StringBuilder sb = new StringBuilder();
	        for (String s2 : args) {
		        sb.append(s2);
		        sb.append(" ");
	        }

	        String message = sb.toString();

            ActiveCommandHandler.handleActiveCommand(player, s.substring(0, 1), message);
        }
        return true;
    }

}
