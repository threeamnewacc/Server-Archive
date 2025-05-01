package net.badlion.arenalobby.listeners;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.RankUpHelper;
import net.badlion.arenalobby.managers.VisibilityManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StateListener extends BukkitUtil.Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Hide the player if in bandwidth saving mode
		if (ArenaLobby.bandwidthSavingMode) {
			for (Player pl : ArenaLobby.getInstance().getServer().getOnlinePlayers()) {
				player.hidePlayer(pl);
				pl.hidePlayer(player);
			}
		} else {
			VisibilityManager.handleLogin(player);
		}

		RankUpHelper.handlePlayerJoin(player);

		final Group group = new Group(player);
		ArenaLobby.getInstance().updatePlayerGroup(player, group);
	}

	// THIS HAS TO BE LAST, EVERYTHING NEEDS TO HAPPEN BEFORE WE REMOVE THEM FROM THE STATE MACHINE
	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			Group group = ArenaLobby.getInstance().getPlayerGroup(event.getPlayer());

			VisibilityManager.removePlayer(event.getPlayer());
			// If party has more players in it then don't remove from state machine
			ArenaLobby.getInstance().handlePlayerLeaveGroup(event.getPlayer(), group);

			ArenaLobby.getInstance().removePlayerGroup(event.getPlayer());
			Gberry.log("GROUP", event.getPlayer().getName() + " removed from group");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

