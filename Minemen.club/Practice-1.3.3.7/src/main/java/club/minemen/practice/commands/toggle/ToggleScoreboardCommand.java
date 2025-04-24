package club.minemen.practice.commands.toggle;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleScoreboardCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public ToggleScoreboardCommand() {
		super("tsb");
		this.setDescription("Toggles a player's ability to see the sidebar.");
		this.setUsage(CC.RED + "Usage: /tsb");
		this.setAliases(Arrays.asList("togglescore", "togglescoreboard", "toggleside", "togglesidebar"));
	}

	@Override
	public boolean execute(CommandSender sender, String s, String[] strings) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		playerData.setScoreboardEnabled(!playerData.isScoreboardEnabled());
		player.sendMessage(playerData.isScoreboardEnabled() ? CC.GREEN + "You can now see the sidebar." : CC.RED + "You can no longer see the sidebar.");
		return true;
	}
}
