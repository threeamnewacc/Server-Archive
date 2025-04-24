package club.minemen.hcfactions.command.parameter;

import club.minemen.core.util.cmd.param.Parameter;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @since 12/2/2017
 */
public class FPlayerParameter extends Parameter<FactionPlayer> {

	@Override
	public FactionPlayer transfer(CommandSender commandSender, String source) {
		if (commandSender instanceof Player && source.equalsIgnoreCase("self")) {
			return FPlayers.getInstance().getByName(commandSender.getName());
		}

		FactionPlayer player = FPlayers.getInstance().getByName(source);
		if (player == null) {
			commandSender.sendMessage(ChatColor.RED + "No player with the name \"" + source + "\" found.");
		}

		return player;
	}
}
