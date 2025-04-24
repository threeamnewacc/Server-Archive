package club.minemen.practice.commands.time;

import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NightCommand extends Command {
	public NightCommand() {
		super("night");
		this.setDescription("Set player time to night.");
		this.setUsage(CC.RED + "Usage: /night");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		((Player) sender).setPlayerTime(18000L, true);
		sender.sendMessage(CC.GREEN + "Time set to night.");

		return true;
	}
}
