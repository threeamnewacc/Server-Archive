package club.minemen.practice.commands.toggle;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleDuelCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public ToggleDuelCommand() {
		super("tdr");
		this.setDescription("Toggles a player's duel requests on or off.");
		this.setUsage(CC.RED + "Usage: /tdr");
		this.setAliases(Arrays.asList("toggleduel", "toggleduels", "td"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		playerData.setAcceptingDuels(!playerData.isAcceptingDuels());
		player.sendMessage(playerData.isAcceptingDuels() ? CC.GREEN + "You are now accepting duel requests." : CC.RED + "You are no longer accepting duel requests.");
		return true;
	}
}
