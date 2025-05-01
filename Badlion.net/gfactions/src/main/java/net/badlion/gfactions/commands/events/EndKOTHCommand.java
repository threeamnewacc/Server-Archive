package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.koth.EndKOTHTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndKOTHCommand implements CommandExecutor {
	
	private GFactions plugin;
	
	public EndKOTHCommand(GFactions plugin) {
		this.plugin = plugin;
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (this.plugin.getKoth() != null) {
                new EndKOTHTask(this.plugin).runTask(this.plugin);
                player.sendMessage(ChatColor.GREEN + "You have ended the KOTH");
            } else {
				player.sendMessage(ChatColor.RED + "There is no currently no KOTH running");
			}
		}
		return true;
	}

}
