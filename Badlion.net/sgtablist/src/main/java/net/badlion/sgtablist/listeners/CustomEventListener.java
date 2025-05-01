package net.badlion.sgtablist.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.bukkitevents.MPGGameStateChangeEvent;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.sgtablist.SGTabList;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.tablist.TabList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.json.simple.JSONObject;

import java.util.UUID;

public class CustomEventListener implements Listener {

	@EventHandler(priority = EventPriority.LASTEST)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player killer = event.getEntity().getKiller();

		// Did they not have a killer?
		if (killer == null) return;

		// Is the killer offline?
		if (!Gberry.isPlayerOnline(killer)) return;

		SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(killer);
		JSONObject sgSettings = UserDataManager.getUserData(killer).getSGSettings();

		// Are they showing their stats?
		if ((boolean) sgSettings.get("stats_visibility")) {
			TabList tabList = SGTabList.getInstance().getSGTabListManager().getTabList(killer);

			tabList.setPosition(27, "§b" + sgPlayer.getTotalKills() + "  ", true);

			tabList.update(false);
		}
	}

	@EventHandler
	public void onMPGGameStateChangeEvent(MPGGameStateChangeEvent event) {
		if (event.getMPGGame().getGameState() == MPGGame.GameState.GAME) {
			// Update player list to show players' real names
			SGTabList.getInstance().getSGTabListManager().updatePlayerList();
		} else if (event.getMPGGame().getGameState() == MPGGame.GameState.POST_GAME) {
			UUID uuid = event.getMPGGame().getWinners().iterator().next();
			Player player = SGTabList.getInstance().getServer().getPlayer(uuid);

			// Exit if player is offline
			if (player == null) return;

			SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(player);
			JSONObject sgSettings = UserDataManager.getUserData(player).getSGSettings();

			// Are they showing their stats?
			if ((boolean) sgSettings.get("stats_visibility")) {
				TabList tabList = SGTabList.getInstance().getSGTabListManager().getTabList(player);

				tabList.setPosition(7, "§b" + sgPlayer.getGamesWon() + " ", true);

				tabList.update(true);
			}

		}
	}

}
