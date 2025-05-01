package net.badlion.survivalgames.listeners;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGCreatePlayerEvent;
import net.badlion.mpg.bukkitevents.MPGEndGameEvent;
import net.badlion.mpg.bukkitevents.MPGServerStateChangeEvent;
import net.badlion.mpg.bukkitevents.MapManagerInitializeEvent;
import net.badlion.mpg.managers.MPGMapManager;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.survivalgames.SGGame;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SGWorld;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.worldrotator.GWorld;
import net.badlion.worldrotator.WorldRotator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.UUID;

public class MPGListener implements Listener {

    @EventHandler
    public void onMapManagerInitialize(MapManagerInitializeEvent event) {
        for (GWorld world : WorldRotator.getInstance().getWorlds()) {
            event.getWorlds().add(new SGWorld(world));
        }
    }

	@EventHandler
	public void onMPGServerStateChangeEvent(MPGServerStateChangeEvent event) {
		if (event.getNewState() == MPG.ServerState.LOBBY) {
			SGGame sgGame = new SGGame(SurvivalGames.getInstance().chooseRandomGamemode());

			// Set the world after because we need access to the SGGame reference when we load the world
			sgGame.setWorld(MPGMapManager.getRandomWorld());
		}
	}

	@EventHandler
	public void onMPGCreatePlayerEvent(MPGCreatePlayerEvent event) {
		SGPlayer sgPlayer = new SGPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
		event.setMpgPlayer(sgPlayer);
	}

	@EventHandler
	public void onMPGEndGameEvent(MPGEndGameEvent event) {
		// Set winner's positions before stats save

		SGGame sgGame = SurvivalGames.getInstance().getSGGame();

		Iterator<UUID> it = sgGame.getWinners().iterator();

		// Is this a clan game?
		if (sgGame.isClanGame()) {
			while (it.hasNext()) {
				UUID uuid = it.next();
				SGPlayer sgPlayer = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid));

				sgPlayer.setPosition(sgGame.decrementPosition());
			}
		} else if (MPG.GAME_TYPE == MPG.GameType.PARTY) { // Is this a party game?
			UUID uuid1 = it.next();
			UUID uuid2 = it.next();

			SGPlayer sgPlayer1 = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid1));
			SGPlayer sgPlayer2 = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid2));

			// Are both players alive at the end?
			if (sgPlayer1.getState() == MPGPlayer.PlayerState.PLAYER && sgPlayer2.getState() == MPGPlayer.PlayerState.PLAYER) {
				// Player 1 and player 2 are alive
				int rating1 = sgGame.getPlayerRating(uuid1);
				int rating2 = sgGame.getPlayerRating(uuid2);

				// Does player 1 have the higher rating?
				if (rating1 > rating2) {
					// Fill player 2's data
					sgPlayer2.setPosition(sgGame.decrementPosition());

					// Fill player 1's data
					sgPlayer1.setPosition(sgGame.decrementPosition());
				} else {
					// Fill player 1's data
					sgPlayer1.setPosition(sgGame.decrementPosition());

					// Fill player 2's data
					sgPlayer2.setPosition(sgGame.decrementPosition());
				}
			} else if (sgPlayer1.getState() == MPGPlayer.PlayerState.PLAYER) {
				// Player 1 is alive
				sgPlayer1.setPosition(sgGame.decrementPosition());
			} else {
				// Player 2 is alive
				sgPlayer2.setPosition(sgGame.decrementPosition());
			}
		} else {
			UUID uuid = it.next();
			SGPlayer sgPlayer = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid));

			sgPlayer.setPosition(sgGame.decrementPosition());
		}
	}

}
