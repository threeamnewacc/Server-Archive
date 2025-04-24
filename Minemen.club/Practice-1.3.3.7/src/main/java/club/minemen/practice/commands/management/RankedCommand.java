package club.minemen.practice.commands.management;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankedCommand extends Command {

	private final Practice plugin = Practice.getInstance();

	public RankedCommand() {
		super("ranked");
		this.setDescription("Manage server ranked mode.");
		this.setUsage(CC.RED + "Usage: /ranked");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.ADMIN)) {
			return true;
		}

		boolean enabled = this.plugin.getQueueManager().isRankedEnabled();

		this.plugin.getQueueManager().setRankedEnabled(!enabled);
		sender.sendMessage(CC.GREEN + "Ranked matches are now " + (!enabled ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.GREEN + ".");

		return true;
	}
}
