package club.minemen.practice.commands;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetStatsCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public ResetStatsCommand() {
		super("resetstats");
		this.setUsage(CC.RED + "Usage: /resetstats [player]");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (commandSender instanceof Player) {
			if (!PlayerUtil.testPermission(commandSender, Rank.ADMIN)) {
				return true;
			}
		}
		if (args.length == 0) {
			commandSender.sendMessage(CC.RED + "Usage: /resetstats <player>");
			return true;
		}
		Player target = this.plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			commandSender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
			return true;
		}

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
		for (Kit kit : this.plugin.getKitManager().getKits()) {
			playerData.setElo(kit.getName(), PlayerData.DEFAULT_ELO);
			playerData.setLosses(kit.getName(), 0);
			playerData.setWins(kit.getName(), 0);
		}
		playerData.setPremiumElo(PlayerData.DEFAULT_ELO);
		playerData.setPremiumLosses(0);
		playerData.setPremiumWins(0);
		commandSender.sendMessage(CC.PRIMARY + "You reset " + CC.SECONDARY + target.getName() + CC.PRIMARY + "'s stats.");
		return true;
	}

}
