package net.badlion.ffa.listeners;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.combattag.events.CombatTagKilledEvent;
import net.badlion.ffa.FFA;
import net.badlion.ffa.FFAPlayer;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.sql.Connection;
import java.sql.SQLException;

import static net.badlion.mpg.managers.MPGPlayerManager.getMPGPlayer;

public class GlobalListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
	    Player player = event.getPlayer();
	    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

	    // Set play start time
	    mpgPlayer.setStartTime(System.currentTimeMillis());

	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
	    player.sendMessage(ChatColor.GOLD + "Welcome to " + ChatColor.AQUA + "Badlion " + FFA.FFA_NAME + " FFA" + ChatColor.GOLD + "!");
	    player.sendMessage(ChatColor.GOLD + "Use " + ChatColor.AQUA + "'/leave'" + ChatColor.GOLD + " to teleport to spawn to avoid a stats loss when quitting!");
	    player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));

	    LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(player.getUniqueId());

	    // No combat logger means they either died, are at spawn, or are logging in for the first time
	    if (loggerNPC == null) {
		    FFA.getInstance().prepPlayerForSpawn(player);

		    // Teleport the player to the spawn location
		    player.teleport(FFA.getInstance().getFFAGame().getWorld().getSpawnLocation());
	    }
    }

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		final FFAPlayer ffaPlayer = (FFAPlayer) getMPGPlayer(event.getPlayer());

		// Track time played
		long totalTimePlayed = (System.currentTimeMillis() - ffaPlayer.getStartTime()) / 1000;
		ffaPlayer.addTotalTimePlayed(totalTimePlayed);

		// Save ministats
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				Connection connection = null;
				try {
					connection = Gberry.getConnection();

					DatabaseManager.savePlayerData(ffaPlayer, connection);

					BukkitUtil.runTask(new Runnable() {
						@Override
						public void run() {
							// Set new past total kills/deaths
							ffaPlayer.setPastTotalKills(ffaPlayer.getPastTotalKills() + ffaPlayer.getKills());
							ffaPlayer.setPastTotalDeaths(ffaPlayer.getPastTotalDeaths() + ffaPlayer.getDeaths());

							// Reset kills, deaths, and killstreak
							ffaPlayer.setKills(0);
							ffaPlayer.setDeaths(0);
							ffaPlayer.setCurrentKillStreak(0);
						}
					});
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(connection);
				}
			}
		});
	}

	@EventHandler
	public void onCombatTagKilledEvent(CombatTagKilledEvent event) {
		// Force save stats for the player to save their death
		final FFAPlayer ffaPlayer = (FFAPlayer) getMPGPlayer(event.getLoggerNPC().getUUID());

		// Save stats
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				Connection connection = null;
				try {
					connection = Gberry.getConnection();

					DatabaseManager.savePlayerData(ffaPlayer, connection);

					BukkitUtil.runTask(new Runnable() {
						@Override
						public void run() {
							// Set new past total kills/deaths
							ffaPlayer.setPastTotalKills(ffaPlayer.getPastTotalKills() + ffaPlayer.getKills());
							ffaPlayer.setPastTotalDeaths(ffaPlayer.getPastTotalDeaths() + ffaPlayer.getDeaths());

							// Reset kills, deaths, and killstreak
							ffaPlayer.setKills(0);
							ffaPlayer.setDeaths(0);
							ffaPlayer.setCurrentKillStreak(0);
						}
					});
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(connection);
				}
			}
		});
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (!(event.getEntity() instanceof Player)) return;

			Player player = ((Player) event.getEntity());

			if (MPGPlayerManager.getMPGPlayer(player).getState() != MPGPlayer.PlayerState.PLAYER) return;

			event.setCancelled(true);

			// Ugly but meh
			if (player.getFallDistance() > 30) {
				// Add to last damage time to prevent them from doing /spawn right away
				MultiKillListener.getInstance().insertLastDamageTime(event.getEntity().getUniqueId());

				// Do they still have the kit selection items?
				if (player.getInventory().first(Material.ENCHANTED_BOOK) != -1) {
					FFA.getInstance().loadKitAutomatically(player);
				}
			}
		}
	}

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }
    }

}
