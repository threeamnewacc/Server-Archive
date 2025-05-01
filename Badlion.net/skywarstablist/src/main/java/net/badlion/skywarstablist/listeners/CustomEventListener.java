package net.badlion.skywarstablist.listeners;

import net.badlion.mpg.MPGGame;
import net.badlion.mpg.bukkitevents.MPGGameStateChangeEvent;
import net.badlion.skywarstablist.SWTabList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {

	@EventHandler
	public void sgGameStartEvent(MPGGameStateChangeEvent event) {
		if (event.getMPGGame().getGameState() == MPGGame.GameState.GAME) {
			SWTabList.getInstance().getSWTabListManager().setGameStarted(true);
		}
	}

	/*@EventHandler
	public void playerDisguiseEvent(PlayerDisguiseEvent event) {
		SWTabList.getInstance().getSWTabListManager().updatePlayerList();
	}*/

}
