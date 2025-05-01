package net.badlion.uhc;

import net.badlion.gberry.AbstractListCommandHandler;
import net.badlion.gberry.Gberry;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UHCListCommandHandler extends AbstractListCommandHandler {

	@Override
	public void handleListCommand(Player player) {
		if (BadlionUHC.getInstance().getConfigurator() == null) {
			player.sendMessage(ChatColor.RED + "Cannot use /list until config has been setup.");
			return;
		}

		String mods = "None";
		StringBuilder sb = new StringBuilder();
		ConcurrentLinkedQueue<UHCPlayer> moderators = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD);
		if (moderators.size() > 0) {
			for (UHCPlayer moderator : moderators) {
				// Only show mods who are online
				Player pl = BadlionUHC.getInstance().getServer().getPlayer(moderator.getUUID());
				if (pl == null) {
					continue;
				}

				sb.append(", ");
				sb.append(ChatColor.RESET);
				sb.append(BadlionUHC.getInstance().getDisplayName(moderator.getUUID()));
			}

			if (sb.length() > 0) {
				mods = sb.substring(2);
			}
		}

		int uhcpOnline = 0;
		for (Player pl : BadlionUHC.getInstance().getServer().getOnlinePlayers()) {
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(pl.getUniqueId());
			if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
				++uhcpOnline;
			}
		}

		player.sendMessage(Gberry.getLineSeparator(ChatColor.RED));
		player.sendMessage(ChatColor.BLUE + "Total Players Online: [" + ChatColor.GOLD + Bukkit.getOnlinePlayers().size() +
				"/" + Bukkit.getServer().getMaxPlayers() + ChatColor.BLUE + "]");
		player.sendMessage(ChatColor.BLUE + "UHC Players Joined/Max: [" + ChatColor.GOLD + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER).size() +
				"/" + BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.MAXPLAYERS.name()).getValue() + ChatColor.BLUE + "]");
		player.sendMessage(ChatColor.BLUE + "UHC Players Online/Total: [" + ChatColor.GOLD + uhcpOnline +
				"/" + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER).size() + ChatColor.BLUE + "]");
		if (BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM) {
			player.sendMessage(ChatColor.BLUE + "UHC Teams Alive: [" + ChatColor.GOLD + UHCTeamManager.getAllAlivePlayingTeams().size() + ChatColor.BLUE + "]");
		}
		player.sendMessage(ChatColor.DARK_RED + "[Host]: " + ChatColor.RESET + (BadlionUHC.getInstance().getHost() != null
				? BadlionUHC.getInstance().getHost().getUsername() : "None"));
		player.sendMessage(ChatColor.DARK_AQUA + "[Moderators]: " + mods);
		player.sendMessage(Gberry.getLineSeparator(ChatColor.RED));
	}

}
