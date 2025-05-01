package net.badlion.factionchat;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.smellychat.commands.handlers.ActiveCommandHandler;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AliasCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
	        Faction faction = FPlayers.i.get(player).getFaction();

            if (faction == null || faction.getId().equals("0")) {
                player.sendMessage(ChatColor.RED + "You are not in a faction!");

	            // They might've just disbanded/left a faction, set global as active channel
	            ChatSettingsManager.getChatSettings(player).setActiveChannel("G");

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
