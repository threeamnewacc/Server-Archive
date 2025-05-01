package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ShowInvisCommand implements CommandExecutor {

	private GFactions plugin;

	public ShowInvisCommand(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if (sender instanceof Player) {
			Scoreboard board = ((Player) sender).getScoreboard();
			Team team = board.registerNewTeam("showinvis");
			team.setCanSeeFriendlyInvisibles(true);

			for (Player p : this.plugin.getServer().getOnlinePlayers()) {
				team.addPlayer(p);
			}
		}
		return true;
	}

}
