package net.badlion.mpg.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LobbyListener implements Listener {

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        // Sanity check if players try to join while server is in loading state
        if (MPG.getInstance().getServerState() == MPG.ServerState.LOADING) {
	        // Spam errors
            for (int a = 0; a < 10; a++) {
                Bukkit.getLogger().info("SERVER IS IN SERVER STATE LOADING, ERROR");
            }
        }
    }

    @EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
    public void onFullServerLogin(PlayerLoginEvent event) {
        // If we don't want to give out this donator feature
        if (!MPG.getInstance().getBooleanOption(MPG.ConfigFlag.KICK_NON_DONATORS_IF_FULL)) {
            return;
        }

        // Don't even bother
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        if (MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.MAX_PLAYERS) == Bukkit.getServer().getOnlinePlayers().size() &&
                    MPG.getInstance().getServerState().ordinal() < MPG.ServerState.GAME.ordinal()) {
            if (event.getPlayer().hasPermission("badlion.donatorplus")) {
                Player lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donator");

                if (lastPlayerJoined != null) {
                    lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator+ to join. Donate to secure a slot.");
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                } else {
                    lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donatorplus");
                    if (lastPlayerJoined != null) {
                        lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator+ to join. Become a Donator+ to secure a slot.");
                        event.setResult(PlayerLoginEvent.Result.ALLOWED);
                    } else {
                        lastPlayerJoined = this.getPlayerWithoutPermission("badlion.lion");
                        if (lastPlayerJoined != null) {
                            lastPlayerJoined.kickPlayer("You have been removed to make room for a Lion to join. Become a Lion to secure a slot.");
                            event.setResult(PlayerLoginEvent.Result.ALLOWED);
                        } else {
                            event.setKickMessage("No lesser ranks found to remove. Unable to join, server full.");
                        }
                    }
                }
            } else if (event.getPlayer().hasPermission("badlion.donator") || (MPG.ALLOW_WHITELISTING && MPG.getInstance().isWhitelisted(event.getPlayer().getName()))) {
                Player lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donator");

                if (lastPlayerJoined != null) {
                    lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator to join. Donate to secure a slot.");
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                } else {
                    event.setKickMessage("No lesser ranks found to remove. Unable to join, server full.");
                }
            }
        }
    }

    private Player getPlayerWithoutPermission(String permission) {
        ConcurrentLinkedQueue<MPGPlayer> mpgPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);

        List<Player> kickables = new ArrayList<>();

        for (MPGPlayer mpgPlayer : mpgPlayers) {
            Player player = mpgPlayer.getPlayer();
            // You can't be kicked if you have the permission, you are whitelisted, or we are preventing teams from being kicked
            if (!player.hasPermission(permission) && !MPG.getInstance().isWhitelisted(player.getDisguisedName())) {
                kickables.add(player);
            }
        }

        if (!kickables.isEmpty()) {
            return kickables.get(kickables.size() - 1);
        }

        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
	    if (MPG.ALLOW_SPECTATING && MPG.getInstance().getMPGGame().getGameState().ordinal() >= MPGGame.GameState.GAME.ordinal()
			    && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.SPECTATOR_ON_LOGIN_AFTER_START)) {
		    MPGPlayerManager.getMPGPlayer(event.getPlayer()).setState(MPGPlayer.PlayerState.SPECTATOR);
	    }

	    if (MPG.getInstance().getServerState() == MPG.ServerState.GAME
			    && MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.LOBBY) {
		    MPG.getInstance().prepPlayer(event.getPlayer());
		    event.getPlayer().teleport(MPG.getInstance().getLobbySpawnLocation());

		    // Get number of players
		    int playersOnline = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER).size();
		    int playersToStart = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.PLAYERS_TO_START);

		    // Not enough players to start?
		    if (playersOnline < playersToStart) {
			    // Is this player in the player state?
			    if (MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState() == MPGPlayer.PlayerState.PLAYER) {
				    // Only send the message if they're not famous (or staff)
				    if (!event.getPlayer().hasPermission("badlion.famous") && !event.getPlayer().hasPermission("badlion.staff")) {
					    Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.GOLD + event.getPlayer().getDisguisedName() + ChatColor.DARK_GREEN + " has joined the server.");
				    }

				    // Broadcast # needed to whole server
				    if (playersToStart - playersOnline > 1) {
					    Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.RED + (playersToStart - playersOnline) + ChatColor.DARK_GREEN + " more players needed to start the game!");
				    } else {
					    Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.RED + "1" + ChatColor.DARK_GREEN + " more player needed to start the game!");
				    }
			    } else {
				    // Player isn't in player state, just send them how many players are needed to start
				    if (playersToStart - playersOnline > 1) {
					    event.getPlayer().sendMessage(MPG.MPG_PREFIX + ChatColor.RED + (playersToStart - playersOnline) + ChatColor.DARK_GREEN + " more players needed to start the game!");
				    } else {
					    event.getPlayer().sendMessage(MPG.MPG_PREFIX + ChatColor.RED + "1" + ChatColor.DARK_GREEN + " more player needed to start the game!");
				    }
			    }
		    } else {
			    // We have enough players, start the game
			    if (MPG.USES_VOTING) {
				    if (!MPG.getInstance().getMPGGame().isVotingEnabled()) {
					    MPG.getInstance().getMPGGame().startVotingTask();
				    }
			    } else {
				    MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.PRE_GAME);
			    }
		    }
	    }
    }

	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerQuitMessage(PlayerQuitEvent event) {
		if (!MPG.USES_MATCHMAKING) {
			if (MPG.getInstance().getServerState() == MPG.ServerState.LOBBY) {
				MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer());
				// Only send the leave message if they're in the player state
				if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
					// Only send the message if they're not famous (or staff)
					if (!event.getPlayer().hasPermission("badlion.famous") && !event.getPlayer().hasPermission("badlion.staff")) {
						Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.GOLD + event.getPlayer().getDisguisedName() + ChatColor.DARK_GREEN + " has left the server.");
					}
				}
			}
		}
	}

}
