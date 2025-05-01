package net.badlion.sglobbytablist.listeners;

import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.sglobbytablist.SGLobbyTabList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerNewScoreboardEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LASTEST)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		ScoreboardUtil.resetScoreboard(player);

		// 1.8 Start
		if (player.getClientVersion().ordinal() >= Player.CLIENT_VERSION.V1_8.ordinal()) {
			//player.setPlayerListHeaderFooter(TabListManager.getInstance().getHeader(), TabListManager.getInstance().getFooter());
		}
		// 1.8 End

		// Create the tab list
		SGLobbyTabList.getInstance().getSGLobbyTabListManager().createTabList(event.getPlayer());
	}

	@EventHandler
	public void onPlayerNewScoreboardEvent(PlayerNewScoreboardEvent event) {
		if (SGLobbyTabList.getInstance().getSGLobbyTabListManager().getTabList(event.getPlayer()) != null) {
			SGLobbyTabList.getInstance().getSGLobbyTabListManager().getTabList(event.getPlayer()).update(true);
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		SGLobbyTabList.getInstance().getSGLobbyTabListManager().removeTabList(player);
	}

}
