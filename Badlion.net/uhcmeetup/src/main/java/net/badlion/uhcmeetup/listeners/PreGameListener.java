package net.badlion.uhcmeetup.listeners;

import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.PreGameCountdownTask;
import net.badlion.uhcmeetup.UHCMeetup;
import net.badlion.uhcmeetup.UHCMeetupGame;
import org.bukkit.entity.Player;
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
		Player player = event.getPlayer();
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());

		UHCMeetupGame uhcMeetupGame = UHCMeetup.getInstance().getUHCMeetupGame();

		// Is this is a spectator joining?
		if (mpgPlayer == null) return;

		// If game has not started and we are a player
		if ((uhcMeetupGame.getGameState() == MPGGame.GameState.PRE_GAME || uhcMeetupGame.getGameState() == MPGGame.GameState.GAME_COUNTDOWN)
				&& mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			// Clear inventory
			player.getInventory().clear();
			player.updateInventory();

			// Give player their kit items
			UHCMeetup.getInstance().giveBuildUHCKitSelectionItems(player.getUniqueId(), player);

			// Teleport the player to their spawn location
			player.teleport(PreGameCountdownTask.getInstance().getPlayerSpawnLocation(player.getUniqueId()));
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
