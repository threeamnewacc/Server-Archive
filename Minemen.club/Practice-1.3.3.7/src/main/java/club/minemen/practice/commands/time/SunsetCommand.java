package club.minemen.practice.commands.time;

import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SunsetCommand extends Command {

	public SunsetCommand() {
		super("sunset");
		this.setDescription("Set player time to sunset.");
		this.setUsage(CC.RED + "Usage: /sunset");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		((Player) sender).setPlayerTime(12000L, true);
		sender.sendMessage(CC.GREEN + "Time set to sunset.");

		return true;
	}
}
