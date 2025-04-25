package cc.patrone.practice.commands.toggle;

import zone.potion.commands.BaseCommand;
import zone.potion.player.rank.Rank;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import org.bukkit.command.CommandSender;

public class ToggleRankedCommand extends BaseCommand {
	private final PracticePlugin plugin;

	public ToggleRankedCommand(PracticePlugin plugin) {
		super("toggleranked", Rank.ADMIN);
		this.plugin = plugin;
	}

	@Override
	protected void execute(CommandSender sender, String[] args) {
		boolean enabled = !plugin.getQueueManager().isRankedEnabled();

		plugin.getQueueManager().setRankedEnabled(enabled);

		sender.sendMessage(CC.PRIMARY + "Ranked matches are now " + (enabled ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.PRIMARY + ".");
	}
}
