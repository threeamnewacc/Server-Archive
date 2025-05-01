package net.badlion.ffa.listeners;

import net.badlion.ffa.FFA;
import net.badlion.ffa.FFAGame;
import net.badlion.ffa.FFAPlayer;
import net.badlion.ffa.FFAWorld;
import net.badlion.ffa.commands.SpawnCommand;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGCreatePlayerEvent;
import net.badlion.mpg.bukkitevents.MPGPlayerStateChangeEvent;
import net.badlion.mpg.bukkitevents.MPGServerStateChangeEvent;
import net.badlion.mpg.bukkitevents.MapManagerInitializeEvent;
import net.badlion.mpg.managers.MPGMapManager;
import net.badlion.worldrotator.WorldRotator;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MPGListener implements Listener {

    @EventHandler
    public void onMapManagerInitialize(MapManagerInitializeEvent event) {
	    event.getWorlds().add(new FFAWorld(WorldRotator.getInstance().getGWorld(FFA.WORLD_NAME)));
    }

	@EventHandler
	public void onMPGServerStateChangeEvent(MPGServerStateChangeEvent event) {
		if (event.getNewState() == MPG.ServerState.LOBBY) {
			// Skip LOBBY state and go to GAME
			event.setNewState(MPG.ServerState.GAME);

			FFAGame game = new FFAGame(((FFAWorld) MPGMapManager.getRandomWorld()));

			// Set the game's state to GAME
			game.setGameState(MPGGame.GameState.GAME);
		}
	}

	@EventHandler
	public void onMPGCreatePlayerEvent(final MPGCreatePlayerEvent event) {
		final FFAPlayer ffaPlayer = new FFAPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
		event.setMpgPlayer(ffaPlayer);

		// Track ministats
		MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().put(event.getPlayer().getUniqueId(), ffaPlayer);

		final UUID uuid = event.getPlayer().getUniqueId();

		// Load their total kills/deaths from database
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				Connection connection = null;

				try {
					connection = Gberry.getConnection();

					MiniStatsPlayer stats = DatabaseManager.getPlayerStats(connection, uuid);

					ffaPlayer.setPastTotalKills(stats.getKills());
					ffaPlayer.setPastTotalDeaths(stats.getDeaths());
					ffaPlayer.setHighestKillStreak(stats.getHighestKillStreak());
				} catch (SQLException e) {
					e.printStackTrace();

					event.getPlayer().sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Error retrieving stats.");
				} finally {
					Gberry.closeComponents(connection);
				}
			}
		});
	}

	@EventHandler
	public void onMPGPlayerStateChangeEvent(MPGPlayerStateChangeEvent event) {
		if (event.getNewState() == MPGPlayer.PlayerState.DC) {
			// Is this player above the spawn platform y limit?
			if (event.getMPGPlayer().getPlayer().getLocation().getY() > FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
				// They didn't jump off spawn platform (or fall far enough), don't spawn the combat logger
				event.setCancelled(true);
			} else {
				Long lastDamageTime = MultiKillListener.getInstance().getLastDamageTime(event.getMPGPlayer().getUniqueId());

				// Is this player not combat tagged?
				if (lastDamageTime == null
						|| lastDamageTime + SpawnCommand.COMBAT_TAG_TIME < System.currentTimeMillis()) {
					event.setCancelled(true);
				}
			}
		} else if (event.getNewState() == MPGPlayer.PlayerState.DEAD) {
			// Set them to PLAYER state
			event.setNewState(MPGPlayer.PlayerState.PLAYER);

			// Handle death manually
			event.getMPGPlayer().handlePlayerDeath();
		}
	}

}
