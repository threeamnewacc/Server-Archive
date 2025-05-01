package net.badlion.arenatablist.lobby.listeners;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.bukkitevents.RankedLeftChangeEvent;
import net.badlion.arenalobby.bukkitevents.RatingChangeEvent;
import net.badlion.arenalobby.bukkitevents.RatingRetrievedEvent;
import net.badlion.arenatablist.ArenaTabList;
import net.badlion.tablist.TabList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaLobbyListener implements Listener {

	@EventHandler
	public void onRatingRetrievedEvent(RatingRetrievedEvent event) {
		// Create the tab list
		Player player = Bukkit.getPlayer(event.getUuid());
		if (player != null) {
			if (ArenaTabList.getInstance().getArenaLobbyTabListManager().getTabList(player) == null) {
				ArenaTabList.getInstance().getArenaLobbyTabListManager().createTabList(player, event.getGlobalRating(), event.getRatings());
			}
		}
	}

	@EventHandler
	public void onRatingChangeEvent(RatingChangeEvent event) {
		// Create the tab list
		Player player = Bukkit.getPlayer(event.getUuid());
		if (player != null) {
			if (ArenaTabList.getInstance().getArenaLobbyTabListManager().getTabList(player) != null) {
				ArenaTabList.getInstance().getArenaLobbyTabListManager().updateTablist(player, event.getGlobalRating(), event.getRatings());
			}
		}
	}

	@EventHandler
	public void onRankedLeftChangeEvent(RankedLeftChangeEvent event) {
		if (event.getPlayer().hasPermission("badlion.donator")) {
			return;
		}

		TabList tabList = ArenaTabList.getInstance().getArenaLobbyTabListManager().getTabList(event.getPlayer());

		if (tabList != null) {

			if (event.getRankedLeft() > 0) {
				tabList.setPosition(4, "§9 " + event.getRankedLeft(), true);
			} else {
				tabList.setPosition(4, "§9 None", true);
			}

			// Update tab list
			tabList.update(false);
		}
	}

}
