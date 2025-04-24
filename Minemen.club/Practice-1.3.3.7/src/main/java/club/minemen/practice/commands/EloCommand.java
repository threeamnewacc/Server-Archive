package club.minemen.practice.commands;

import club.minemen.core.util.finalutil.CC;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EloCommand extends Command {
	public EloCommand() {
		super("elo");
		this.setDescription("View a player's Elo.");
		this.setUsage(CC.RED + "Usage: /elo [player]");
		this.setAliases(Arrays.asList("stats", "lb", "leaderboard", "leaderboards"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		sender.sendMessage(CC.PRIMARY + "Visit " + CC.SECONDARY + "http://minemen.club " + CC.PRIMARY + "to view the leaderboards.");
		if (args.length == 0) {
			sender.sendMessage(CC.PRIMARY + "Here are your stats: " + CC.PRIMARY + "http://minemen.club/user/" + sender.getName());
		} else {
			sender.sendMessage(CC.SECONDARY + args[0] + CC.PRIMARY + "'s stats: " + CC.SECONDARY + "http://minemen.club/user/" + args[0]);
		}
		return true;
	}
}
