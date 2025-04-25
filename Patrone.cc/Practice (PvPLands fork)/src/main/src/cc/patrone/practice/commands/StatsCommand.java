package cc.patrone.practice.commands;

import zone.potion.commands.PlayerCommand;
import cc.patrone.practice.PracticePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public StatsCommand(PracticePlugin plugin) {
		super("stats");
		this.plugin = plugin;
	}

	@Override
	public void execute(Player player, String[] args) {
		Player target = args.length >= 1 && Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]) : player;
		plugin.getPlayerManager().displayStats(player, target);
	}
}
