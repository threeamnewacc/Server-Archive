package net.badlion.arenapvp.listener;

import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.PotPvPPlayer;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.helper.PlayerHelper;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.manager.PotPvPPlayerManager;
import net.badlion.arenapvp.manager.SidebarManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.matchmaking.MatchNPCLogger;
import net.badlion.arenapvp.matchmaking.RedRoverBattle;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.events.CombatTagCreateEvent;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class JoinLeaveRespawnListener implements Listener {

	/*
	@EventHandler(priority = EventPriority.LAST)
	public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
		if (!GPermissions.getInstance().userHasPermission(event.getUniqueId().toString(), "badlion.donator")) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Not Whitelisted:" + ChatColor.GOLD + " Buy a rank at http://store.badlion.net/ to join the closed beta.");
		}
	}*/

	@EventHandler(priority = EventPriority.FIRST)
	public void onJoinFirst(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (event.getPlayer().isOp()) {
			event.getPlayer().setOp(false);
		}

		if (event.getPlayer().hasPermission("badlion.kittrial")) {
			ArenaPvP.getInstance().addMuteBanPerms(player);
		}
		PotPvPPlayer potPvPPlayer = new PotPvPPlayer();
		PotPvPPlayerManager.players.put(player.getUniqueId(), potPvPPlayer);

		TeamStateMachine.getInstance().setCurrentState(player, TeamStateMachine.loginState);
		TeamStateMachine.loginState.add(player, true);

		State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(player);

		if (MatchManager.getMatchesAwaitingPlayers().containsKey(player.getUniqueId()) || MatchManager.getCombatLoggedPlayers().containsKey(player.getUniqueId())) {
			Match match = MatchManager.getMatchesAwaitingPlayers().get(player.getUniqueId());
			boolean combatLogged = false;
			if (match == null) {
				Bukkit.getLogger().log(Level.INFO, "MATCH LOGGER LOGIN: " + player.getName());
				combatLogged = true;
				Team team = MatchManager.getCombatLoggedPlayers().remove(player.getUniqueId());
				match = MatchManager.getActiveMatches().get(team);
				match.updateScoreboards(player);
				MatchNPCLogger logger = (MatchNPCLogger) CombatTagPlugin.getInstance().getLogger(player.getUniqueId());
				logger.restorePlayer(player, match);
				match.getKitRuleSet().applyKnockbackToPlayer(player);
				final Match finalMatch = match;
				new BukkitRunnable() {
					@Override
					public void run() {
						SidebarManager.addSidebarItems(player, finalMatch);
					}
				}.runTaskLater(ArenaPvP.getInstance(), 1);
			}

			Bukkit.getLogger().log(Level.INFO, "MATCH LOGIN: " + match.toString());
			try {
				currentState.transition(TeamStateMachine.matchState, event.getPlayer());
				TeamStateMachine.matchState.setPlayerMatch(event.getPlayer(), match);
				if (match instanceof RedRoverBattle) {
					if (!combatLogged) {
						TeamStateMachine.matchState.push(TeamStateMachine.redRoverWaitingState, event.getPlayer());
					}
				}
			} catch (IllegalStateTransitionException e) {
				e.printStackTrace();
				return;
			}
			if (!(match instanceof RedRoverBattle)) {
				ArenaPvP.getInstance().setPlayerTeam(player, match.getPlayersTeam(player));
			} else {
				RedRoverBattle battle = (RedRoverBattle) match;
				if (combatLogged) {
					Bukkit.getLogger().log(Level.INFO, "REDROVER LOGIN COMBAT LOGGER: " + player.getName());

					if (battle.getFighter1UUID().equals(player.getUniqueId())) {
						battle.setFighter1(player);
					} else if (battle.getFighter2UUID().equals(player.getUniqueId())) {
						battle.setFighter2(player);
					}
				}
				if (battle.getTeam1().contains(player)) {
					ArenaPvP.getInstance().setPlayerTeam(player, battle.getTeam1());
				} else if (battle.getTeam2().contains(player)) {
					ArenaPvP.getInstance().setPlayerTeam(player, battle.getTeam2());
				}
			}
			return;
		}

		if (MatchManager.getRedRoverLoggedOutPlayers().containsKey(player.getUniqueId())) {
			Bukkit.getLogger().log(Level.INFO, "REDROVER LOGIN: " + player.getName());

			RedRoverBattle redRoverBattle = (RedRoverBattle) MatchManager.getRedRoverLoggedOutPlayers().remove(player.getUniqueId());

			redRoverBattle.updateScoreboards(player);
			redRoverBattle.getKitRuleSet().applyKnockbackToPlayer(player);
			new BukkitRunnable() {
				@Override
				public void run() {
					SidebarManager.addSidebarItems(player, redRoverBattle);
				}
			}.runTaskLater(ArenaPvP.getInstance(), 1);

			try {
				currentState.transition(TeamStateMachine.matchState, event.getPlayer());
				TeamStateMachine.matchState.setPlayerMatch(event.getPlayer(), redRoverBattle);
				TeamStateMachine.matchState.push(TeamStateMachine.redRoverWaitingState, event.getPlayer());
			} catch (IllegalStateTransitionException e) {
				e.printStackTrace();
				return;
			}

			if (redRoverBattle.getTeam1().contains(player)) {
				ArenaPvP.getInstance().setPlayerTeam(player, redRoverBattle.getTeam1());
				Gberry.safeTeleport(player, redRoverBattle.getArena().getWarp1Origin());

			} else if (redRoverBattle.getTeam2().contains(player)) {
				ArenaPvP.getInstance().setPlayerTeam(player, redRoverBattle.getTeam2());
				Gberry.safeTeleport(player, redRoverBattle.getArena().getWarp2Origin());
			} else {
				Gberry.safeTeleport(player, redRoverBattle.getArena().getWarp1Origin());
			}
			return;
		}

		SpectatorHelper.activateSpectateGameMode(event.getPlayer());
		player.sendFormattedMessage("{0}Spectator mode enabled. Use {1} to a player.", ChatColor.GREEN, "/sp [player] to teleport");
		// Teleport to spawn if they are a spectator
		Gberry.safeTeleport(player, ArenaPvP.getInstance().getSpawnLocation());
		if (event.getPlayer().isDead()) {
			event.getPlayer().spigot().respawn();
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onJoinLast(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (MatchManager.getMatchesAwaitingPlayers().containsKey(player.getUniqueId())) {
			Match match = MatchManager.getMatchesAwaitingPlayers().get(player.getUniqueId());
			if (match instanceof RedRoverBattle) {
				if (player.isDead()) {
					player.spigot().respawn();
				}
				Gberry.safeTeleport(player, match.getArena().getWarp1Origin());
				match.checkIn(player);
			} else {
				if (match.contains(event.getPlayer())) {
					if (player.isDead()) {
						player.spigot().respawn();
					}
					PlayerHelper.healAndPrepPlayerForBattle(player);
					// Teleport before check-in to make sure they are at the arena already
					Gberry.safeTeleport(player, match.getWarpForPlayer(player));
					match.checkIn(player);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		SpectatorListener.cleanupCooldownMaps(player);
		TeamStateMachine.spectatorState.removeSpectatorMatch(player);

		PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());

		Team team = ArenaPvP.getInstance().getPlayerTeam(event.getPlayer());
		Match match;
		if (team == null) {
			// Try and get their match from match state if they are not on a team (Redrover)
			match = MatchState.getPlayerMatch(player);
		} else {
			match = MatchManager.getActiveMatches().get(team);
		}
		if (match != null) {
			TeamStateMachine.matchState.removePlayerMatch(player);
			if (!match.isOver()) {
				if (match instanceof RedRoverBattle) {
					RedRoverBattle redRoverBattle = (RedRoverBattle) match;
					redRoverBattle.handleLogout(player);
					Bukkit.getLogger().log(Level.INFO, "REDROVER LOGOUT: " + player.getName());
				}
				if (match.getPlayersTeam(player) != null) {
					Team playersTeam = match.getPlayersTeam(player);
					// Make sure the player is alive in the match, they could be spectator from a party fight
					if (TeamStateMachine.matchState.contains(player)) {
						if (playersTeam.isActive(player)) {
							if (match instanceof RedRoverBattle) {
								RedRoverBattle redRoverBattle = (RedRoverBattle) match;
								// Do not spawn a logger if they are not fighting in the red rover match
								if (redRoverBattle.getFighter1() != player && redRoverBattle.getFighter2() != player) {
									Bukkit.getLogger().log(Level.INFO, "REDROVER COMBAT LOG: " + player.getName());

									CombatTagPlugin.getInstance().removeCombatTagged(player.getUniqueId());
									ArenaPvP.getInstance().removePlayerTeam(event.getPlayer());
									KitCommon.inventories.remove(event.getPlayer().getUniqueId());
									PotPvPPlayerManager.players.remove(event.getPlayer().getUniqueId());
									return;
								}
							} else {
								match.handleLogout(player);
							}

							// Check if there are no players left in the match, if there are none just tie it
							if (match.checkTie(player)) {
								CombatTagPlugin.getInstance().removeCombatTagged(player.getUniqueId());
								ArenaPvP.getInstance().removePlayerTeam(event.getPlayer());
								KitCommon.inventories.remove(event.getPlayer().getUniqueId());
								PotPvPPlayerManager.players.remove(event.getPlayer().getUniqueId());
								return;
							}

							// Store players team for if they log back in
							MatchManager.getCombatLoggedPlayers().put(player.getUniqueId(), team);
							ArenaPvP.getInstance().removePlayerTeam(event.getPlayer());

							// Spawn Logger
							CombatTagPlugin.getInstance().addCombatTagged(player.getUniqueId());
							// Store players stats incase logger wins, if logger dies it will store them.
							match.storePlayerStats(player);
							match.broadcastMessage(ChatColor.RED + "(CombatLogger) " + player.getDisguisedName() + ChatColor.GOLD
									+ ": They have 45 seconds to join back until their logger dies.");

							// Load their kit if they logout while selecting kit
							if (potPvPPlayer.isSelectingKit()) {
								KitCommon.loadKit(player, match.getKitRuleSet(), 0);
								potPvPPlayer.setSelectingKit(false);
							}

							KitCommon.inventories.remove(event.getPlayer().getUniqueId());
							PotPvPPlayerManager.players.remove(event.getPlayer().getUniqueId());
							return;
						}
					}
				}
			}
		}

		KitCommon.inventories.remove(event.getPlayer().getUniqueId());
		PotPvPPlayerManager.players.remove(event.getPlayer().getUniqueId());
		CombatTagPlugin.getInstance().removeCombatTagged(player.getUniqueId());
		// Always remove player team since it is storing the player object
		ArenaPvP.getInstance().removePlayerTeam(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerQuitLast(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		TeamStateMachine.getInstance().cleanupElement(player);
	}


	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(event.getPlayer());
		if (currentState.equals(TeamStateMachine.spectatorState)) {
			event.setRespawnLocation(player.getLocation());
			return;
		}
		Team team = ArenaPvP.getInstance().getPlayerTeam(player);
		if (MatchManager.getMatchesAwaitingPlayers().containsKey(player.getUniqueId())) {
			Match match = MatchManager.getMatchesAwaitingPlayers().get(player.getUniqueId());
			event.setRespawnLocation(match.getWarpForPlayer(player));
			return;
		}
		if (currentState.equals(TeamStateMachine.matchState)) {
			Match match = MatchState.getPlayerMatch(player);
			if (match != null) {
				if (match.isOver() || !match.isStarted()) {
					return;
				}
				// Make them into a spectator since the match is not over
				event.setRespawnLocation(match.getWarpForPlayer(player));
			}
		}
		if (currentState.equals(TeamStateMachine.redRoverWaitingState)) {
			Match match = MatchManager.getActiveMatches().get(team);
			event.setRespawnLocation(match.getWarpForPlayer(player));
		}
	}

	@EventHandler
	public void onCombatTagCreate(CombatTagCreateEvent event) {
		MatchNPCLogger logger = new MatchNPCLogger(event.getPlayer());
		event.setLoggerNPC(logger);
		logger.resetDespawnTimer();
	}

}
