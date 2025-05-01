package net.badlion.survivalgames.listeners;

import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.PreGameCountdownTask;
import net.badlion.survivalgames.SGGame;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PreGameListener implements Listener {

	@EventHandler(priority= EventPriority.LAST)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		SGGame sgGame = SurvivalGames.getInstance().getSGGame();

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());

		// Is this is a spectator joining?
		if (mpgPlayer == null) return;

		// If game has not started and we are a player
		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER
				&& (sgGame.getGameState() == MPGGame.GameState.PRE_GAME || sgGame.getGameState() == MPGGame.GameState.GAME_COUNTDOWN)) {
			// Clear inventory
			event.getPlayer().getInventory().clear();
			event.getPlayer().updateInventory();

			// Teleport the player to their spawn location
			event.getPlayer().teleport(PreGameCountdownTask.getInstance().getPlayerSpawnLocation(event.getPlayer().getUniqueId()));
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
	    event.setCancelled(true);
    }

}
