package club.minemen.practice.commands.time;

import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCommand extends Command {
	public DayCommand() {
		super("day");
		this.setDescription("Set player time to day.");
		this.setUsage(CC.RED + "Usage: /day");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		((Player) sender).setPlayerTime(6000L, true);
		sender.sendMessage(CC.GREEN + "Time set to day.");

		return true;
	}
}
