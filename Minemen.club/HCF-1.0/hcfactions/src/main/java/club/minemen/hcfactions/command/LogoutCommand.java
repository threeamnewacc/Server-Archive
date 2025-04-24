package club.minemen.hcfactions.command;

import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.type.LogoutTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @since 12/3/2017
 */
@RequiredArgsConstructor
public class LogoutCommand implements CommandHandler {

	private final HCFactions plugin;

	@Command(name = "logout", description = "Logs you out safely")
	public void logoutCommand(Player player) {
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		if (factionPlayer.isPvpTagged()) {
			player.sendFormattedMessage("{0}You''re unable to logout while pvp tagged.", ChatColor.RED);
			return;
		}

		if (HCFactions.getInstance().getLogoutTasks().containsKey(player)) {
			player.sendFormattedMessage("{0}You''re already logging out!", ChatColor.RED);
			return;
		}

		HCFactions.getInstance().getLogoutTasks().put(player, new LogoutTask(HCFactions.getInstance(), player));
	}

}
