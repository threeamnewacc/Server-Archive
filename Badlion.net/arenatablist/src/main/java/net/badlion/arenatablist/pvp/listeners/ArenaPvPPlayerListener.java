package net.badlion.arenatablist.pvp.listeners;

import net.badlion.arenatablist.ArenaTabList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerNewScoreboardEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaPvPPlayerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// 1.8 Start
		if (player.getClientVersion().ordinal() >= Player.CLIENT_VERSION.V1_8.ordinal()) {
			//player.setPlayerListHeaderFooter(TabListManager.getInstance().getHeader(), TabListManager.getInstance().getFooter());
		}
		// 1.8 End

		// We don't create the tab list here, we create it in the rating retrieved event listener
	}

	@EventHandler
	public void onPlayerNewScoreboardEvent(PlayerNewScoreboardEvent event) {
		if (ArenaTabList.getInstance().getArenaPvPTabListManager().getTabList(event.getPlayer()) != null) {
			ArenaTabList.getInstance().getArenaPvPTabListManager().getTabList(event.getPlayer()).update(true);
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		ArenaTabList.getInstance().getArenaPvPTabListManager().removeTabList(player);
	}

}