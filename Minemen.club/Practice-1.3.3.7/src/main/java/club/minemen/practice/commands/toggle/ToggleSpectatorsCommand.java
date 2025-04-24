package club.minemen.practice.commands.toggle;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleSpectatorsCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public ToggleSpectatorsCommand() {
		super("tsp");
		this.setDescription("Toggles a player's ability to spectate you on or off.");
		this.setUsage(CC.RED + "Usage: /tsp");
		this.setAliases(Arrays.asList("togglesp", "togglespec", "togglespectator", "togglespectators"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		playerData.setAllowingSpectators(!playerData.isAllowingSpectators());
		player.sendMessage(playerData.isAllowingSpectators() ? CC.GREEN + "You are now allowing spectators." : CC.RED + "You are no longer allowing spectators.");
		return true;
	}
}
