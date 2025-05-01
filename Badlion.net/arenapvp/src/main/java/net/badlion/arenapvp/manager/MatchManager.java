package net.badlion.arenapvp.manager;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.PotPvPPlayer;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.arenas.Arena;
import net.badlion.arenapvp.helper.DeathHelper;
import net.badlion.arenapvp.helper.PlayerHelper;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.listener.MCPListener;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.matchmaking.RedRoverBattle;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MatchManager {

	//Key is a member, value is the match they need to go to.
	private static Map<UUID, Match> matchesAwaitingPlayers = new HashMap<>();
	private static Map<UUID, Team> combatLoggedPlayers = new HashMap<>();

	private static Map<UUID, Match> redRoverLoggedOutPlayers = new HashMap<>();

	private static Map<Team, Match> activeMatches = new HashMap<>();


	// Called from MCP event, should create a duel object add the teams to it and add it to duelsAwaitingPlayers, also pick an arena
	public static void handleNewMatchs(JSONObject jsonObject) {
		if (jsonObject == null) {
			return;
		}
		List<JSONObject> pendingMatches = (List<JSONObject>) jsonObject.get("pending_matches");
		if (pendingMatches != null) {
			for (JSONObject pendingMatch : pendingMatches) {
				MatchManager.handlePendingMatchData(pendingMatch);
			}
		}
	}


	public static void handlePendingMatchData(JSONObject pendingMatch) {
		Bukkit.getLogger().log(Level.INFO, "[New Pending match data]: " + pendingMatch.toString());
		final UUID matchId = UUID.fromString((String) pendingMatch.get("match_id"));

		for (Match match : MatchManager.getMatchesAwaitingPlayers().values()) {
			if (match.getMatchUuid().equals(matchId)) {
				Bukkit.getLogger().log(Level.INFO, "Duplicate pending match came in for already active match. Ignoring it.");
				return;
			}
		}
		for (Match match : MatchManager.getActiveMatches().values()) {
			if (match.getMatchUuid().equals(matchId)) {
				Bukkit.getLogger().log(Level.INFO, "Duplicate pending match came in for already active match. Ignoring it.");
				return;
			}
		}
		final String arenaName = (String) pendingMatch.get("arena_name");
		String ladder = (String) pendingMatch.get("ladder");

		String type = (String) pendingMatch.get("type");
		ArenaCommon.LadderType ladderType = null;
		for (ArenaCommon.LadderType ladderType1 : ArenaCommon.LadderType.values()) {
			if (ladderType1.getTag().equals(type)) {
				ladderType = ladderType1;
			}
		}
		final JSONObject team1IdStrings = (JSONObject) pendingMatch.get("team_1");
		JSONObject team2IdStrings = (JSONObject) pendingMatch.get("team_2");

		final List<String> bothTeamStrings = new ArrayList<>();
		bothTeamStrings.addAll((Collection<? extends String>) team1IdStrings.get("uuids"));
		bothTeamStrings.addAll((Collection<? extends String>) team2IdStrings.get("uuids"));

		boolean onePlayerOnline = false;
		boolean allPlayersOnline = true;
		final List<UUID> team1Members = new ArrayList<>();
		for (String playerId : (List<String>) team1IdStrings.get("uuids")) {
			final UUID memberId = UUID.fromString(playerId);

			if (!KitCommon.inventories.containsKey(memberId)) {
				KitCommon.inventories.put(memberId, new HashMap<>());
			}

			PotPvPPlayer potPvPPlayer = new PotPvPPlayer();
			PotPvPPlayerManager.players.put(memberId, potPvPPlayer);

			team1Members.add(memberId);
			Player member = Bukkit.getPlayer(memberId);
			if (member == null) {
				allPlayersOnline = false;
			} else {
				onePlayerOnline = true;
			}
		}

		final List<UUID> team2Members = new ArrayList<>();
		for (String playerId : (List<String>) team2IdStrings.get("uuids")) {
			final UUID memberId = UUID.fromString(playerId);

			if (!KitCommon.inventories.containsKey(memberId)) {
				KitCommon.inventories.put(memberId, new HashMap<>());
			}

			PotPvPPlayer potPvPPlayer = new PotPvPPlayer();
			PotPvPPlayerManager.players.put(memberId, potPvPPlayer);

			team2Members.add(memberId);

			Player member = Bukkit.getPlayer(memberId);
			if (member == null) {
				allPlayersOnline = false;
			} else {
				onePlayerOnline = true;
			}
		}

		// For either of these, only team1 will have players, we need to shuffle it and then split it for party team, for ffa just leave on 1 team
		int size = (int) Math.ceil(team1Members.size() / 2D); // -1 because of the leader

		UUID leaderId = null;
		switch (ladderType) {
			case PARTY_RED_ROVER_BATTLE:
				leaderId = UUID.fromString((String) pendingMatch.get("party_leader"));
				break;
			case PARTY_FFA:
				break;
			case PARTY_TEAM:
				// Mix the teams
				Collections.shuffle(team1Members);
				// Add people to second team (if main party uneven, add extra person to random party)
				for (int i = 0; i < size; i++) {
					UUID playerId = team1Members.get(i);

					// Is this the last person we're cycling through?
					if (i == size - 1) {
						if (team2Members.size() + 1 == team1Members.size()) {
							// Randomly assign the extra person to one of the parties
							if (Math.random() < 0.5D) { // Use Math.random() to avoid making/storing a Random object
								// Keep person in leader's party
								break;
							}
						}
					}
					// Leave tean
					team1Members.remove(playerId);
					team2Members.add(playerId);
				}
				Bukkit.getLogger().log(Level.INFO, "TEAM1: " + team1Members);
				Bukkit.getLogger().log(Level.INFO, "TEAM2: " + team2Members);
				break;
		}


		boolean friendlyFireEnabled = false;

		final JSONObject extraData = (JSONObject) pendingMatch.get("extra_data");
		if (extraData != null) {
			if (extraData.containsKey("friendly_fire")) {
				String friendlyFire = (String) extraData.get("friendly_fire");
				if (friendlyFire.equalsIgnoreCase("true")) {
					friendlyFireEnabled = true;
				}
			}
		}
		final KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(ladder);

		final boolean finalFriendlyFireEnabled = friendlyFireEnabled;

		// New match for players, probably a best of match/tournament/clan ladder
		if (allPlayersOnline) {
			final ArenaCommon.LadderType finalLadderType1 = ladderType;
			new BukkitRunnable() {
				@Override
				public void run() {
					Connection connection = null;
					try {
						connection = Gberry.getConnection();
						Map<UUID, List<Kit>> kits = KitCommon.getAllKitContentsForPlayersAndRuleset(connection, bothTeamStrings, kitRuleSet);
						for (UUID playerId : kits.keySet()) {
							//Bukkit.getLogger().log(Level.INFO, "Kit Loaded: " + playerId + " amount: " + kits.get(playerId).size());
							KitType kitType = new KitType(playerId.toString(), kitRuleSet.getName());
							if (KitCommon.inventories.get(playerId) == null) {
								KitCommon.inventories.put(playerId, new HashMap<>());
							}
							KitCommon.inventories.get(playerId).put(kitType, kits.get(playerId));
						}

						new BukkitRunnable() {
							@Override
							public void run() {
								handleMatchWithPlayersOnline(team1Members, team2Members, arenaName, kitRuleSet, finalLadderType1, matchId, extraData, team1IdStrings, team2IdStrings, finalFriendlyFireEnabled);
							}
						}.runTask(ArenaPvP.getInstance());
					} catch (SQLException ex) {
						ex.printStackTrace();
					} finally {
						Gberry.closeComponents(connection);
					}
				}
			}.runTaskAsynchronously(ArenaPvP.getInstance());

			// Go to next pending match
			return;
		}

		final ArenaCommon.LadderType finalLadderType = ladderType;
		final UUID finalLeaderId = leaderId;
		final boolean finalOnePlayerOnline = onePlayerOnline;
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Map<UUID, List<Kit>> kits = KitCommon.getAllKitContentsForPlayersAndRuleset(Gberry.getConnection(), bothTeamStrings, kitRuleSet);
					for (UUID playerId : kits.keySet()) {
						//Bukkit.getLogger().log(Level.INFO, "Kit Loaded: " + playerId + " amount: " + kits.get(playerId).size());
						KitType kitType = new KitType(playerId.toString(), kitRuleSet.getName());
						if (KitCommon.inventories.get(playerId) == null) {
							KitCommon.inventories.put(playerId, new HashMap<>());
						}
						KitCommon.inventories.get(playerId).put(kitType, kits.get(playerId));
					}
					double team1Rank = -1.0;
					double team2Rank = -1.0;
					if (finalLadderType.equals(ArenaCommon.LadderType.RANKED_1V1)) {
						team1Rank = RatingManager.getDBUserRatings(Gberry.getConnection(), team1Members.get(0), kitRuleSet.getId());
						team2Rank = RatingManager.getDBUserRatings(Gberry.getConnection(), team2Members.get(0), kitRuleSet.getId());
					}

					final RatingUtil.Rank finalTeam1Rank = RatingUtil.Rank.getRankByElo(team1Rank);
					final boolean team1DemoGame = RatingUtil.Rank.isDemotionGame(team1Rank);
					final boolean team1PromoGame = RatingUtil.Rank.isPromotionGame(team1Rank);

					final RatingUtil.Rank finalTeam2Rank = RatingUtil.Rank.getRankByElo(team2Rank);
					final boolean team2DemoGame = RatingUtil.Rank.isDemotionGame(team2Rank);
					final boolean team2PromoGame = RatingUtil.Rank.isPromotionGame(team2Rank);

					Bukkit.getLogger().info("MATCH: T1: " + finalTeam1Rank.getName() + " - " + team1DemoGame + " - " + team1PromoGame + " T2:" + finalTeam2Rank.getName() + " - " + team2DemoGame + " - " + team2PromoGame);

					new BukkitRunnable() {
						@Override
						public void run() {
							Arena arena = ArenaManager.getArenaByName(arenaName);
							arena.setBeingUsed(true);
							Bukkit.getLogger().log(Level.INFO, "ARENA: " + arena.getArenaName() + " origin=" + arena.getOrigin() + " warp1=" + arena.getWarp1() + " war2=" + arena.getWarp2());

							Match match;
							if (finalLadderType.equals(ArenaCommon.LadderType.PARTY_RED_ROVER_BATTLE)) {
								match = new RedRoverBattle(arena, false, kitRuleSet, UUID.randomUUID(), finalLeaderId, team1Members);
								match.setTeam1Info(team1IdStrings);
								match.setTeam2Info(team2IdStrings);
								match.setExtraData(extraData);
								match.setLadderType(finalLadderType);
							} else if (finalLadderType.equals(ArenaCommon.LadderType.PARTY_RED_ROVER_DUEL)) {
								match = new RedRoverBattle(arena, false, kitRuleSet, UUID.randomUUID(), team1Members, team2Members, true);
								match.setTeam1Info(team1IdStrings);
								match.setTeam2Info(team2IdStrings);
								match.setExtraData(extraData);
								match.setLadderType(finalLadderType);
							} else {
								match = new Match(arena, finalLadderType.isRanked(), kitRuleSet, matchId);
								match.setFriendlyFireEnabled(finalFriendlyFireEnabled);
								match.setTeam1Info(team1IdStrings);
								match.setTeam2Info(team2IdStrings);
								match.setExtraData(extraData);
								match.setLadderType(finalLadderType);
								if (finalLadderType.equals(ArenaCommon.LadderType.RANKED_1V1)) {
									if (finalTeam1Rank != null && finalTeam2Rank != null) {
										match.setTeam1Rank(finalTeam1Rank, team1DemoGame, team1PromoGame);
										match.setTeam2Rank(finalTeam2Rank, team2DemoGame, team2PromoGame);
									}
								}

								List<Team> teams = new ArrayList<>();
								if (finalLadderType.equals(ArenaCommon.LadderType.PARTY_FFA)) {
									int i = 1;
									for (UUID playerId : team1Members) {
										Team team = new Team(playerId);
										team.setName("team_" + i++);
										teams.add(team);
									}
								} else {
									Team team1 = new Team(team1Members, "team_1");
									Team team2 = new Team(team2Members, "team_2");
									teams.add(team1);
									teams.add(team2);
								}
								match.prepGame(teams);
							}

							if (finalOnePlayerOnline) {
								List<UUID> allPlayers = new ArrayList<>();
								allPlayers.addAll(team1Members);
								allPlayers.addAll(team2Members);
								for (UUID playerId : allPlayers) {
									Player member = Bukkit.getPlayer(playerId);
									if (member != null) {
										if (!TeamStateMachine.matchState.contains(member)) {
											State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(member);
											if (TeamStateMachine.spectatorState.contains(member)) {
												SpectatorHelper.disableSpectateGameMode(member);
											}
											if (TeamStateMachine.deathState.contains(member)) {
												DeathHelper.disableDeathMode(member);
											}
											try {
												currentState.transition(TeamStateMachine.matchState, member);
											} catch (IllegalStateTransitionException e) {
												e.printStackTrace();
											}
										}
										TeamStateMachine.matchState.setPlayerMatch(member, match);

										ArenaPvP.getInstance().setPlayerTeam(member, match.getPlayersTeam(member));

										PlayerHelper.healAndPrepPlayerForBattle(member);
										// Teleport before check-in to make sure they are at the arena already
										Gberry.safeTeleport(member, match.getWarpForPlayer(member));
										match.checkIn(member);
										member.sendFormattedMessage("{0}One of the players in your match is not online, wait for them to join back. The match will tie in 30 seconds.", ChatColor.GOLD);
									}
								}
							}

							for (UUID playerId : team1Members) {
								matchesAwaitingPlayers.put(playerId, match);
							}
							for (UUID playerId : team2Members) {
								matchesAwaitingPlayers.put(playerId, match);
							}

							JSONObject data = new JSONObject();
							data.put("server_name", Gberry.serverName);
							data.put("match_uuid", match.getMatchUuid().toString());
							data.put("team_1", team1IdStrings);
							data.put("team_2", team2IdStrings);
							List<JSONObject> currentSetupMatches = MCPListener.data.get("setup_matches");
							if (currentSetupMatches == null) {
								currentSetupMatches = new ArrayList<>();
							}
							currentSetupMatches.add(data);
							MCPListener.data.put("setup_matches", currentSetupMatches);
						}
					}.runTask(ArenaPvP.getInstance());

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(ArenaPvP.getInstance());
	}


	public static void handleMatchWithPlayersOnline(List<UUID> team1Members, List<UUID> team2Members, String arenaName, KitRuleSet kitRuleSet, ArenaCommon.LadderType ladderType, UUID matchId, JSONObject extraData, JSONObject team1IdStrings, JSONObject team2IdStrings, boolean friendlyFire) {
		Team team1 = new Team(team1Members, "team_1");
		Team team2 = new Team(team2Members, "team_2");

		Arena arena = ArenaManager.getArenaByName(arenaName);
		arena.setBeingUsed(true);
		Bukkit.getLogger().log(Level.INFO, "ARENA: " + arena.getArenaName() + " origin=" + arena.getOrigin() + " warp1=" + arena.getWarp1() + " war2=" + arena.getWarp2());
		Match match = new Match(arena, false, kitRuleSet, matchId);
		match.setFriendlyFireEnabled(friendlyFire);
		match.setTeam1Info(team1IdStrings);
		match.setTeam2Info(team2IdStrings);
		match.prepGame(team1, team2);
		match.setExtraData(extraData);
		match.setLadderType(ladderType);

		for (UUID memberId : team1Members) {
			Player member = Bukkit.getPlayer(memberId);
			if (member != null) {
				if (member.isDead()) {
					member.spigot().respawn();
				}
				if (!TeamStateMachine.matchState.contains(member)) {
					State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(member);
					if (TeamStateMachine.spectatorState.contains(member)) {
						SpectatorHelper.disableSpectateGameMode(member);
					}
					if (TeamStateMachine.deathState.contains(member)) {
						DeathHelper.disableDeathMode(member);
					}
					try {
						currentState.transition(TeamStateMachine.matchState, member);
					} catch (IllegalStateTransitionException e) {
						e.printStackTrace();
					}
				}
				TeamStateMachine.matchState.setPlayerMatch(member, match);

				ArenaPvP.getInstance().setPlayerTeam(member, match.getPlayersTeam(member));

				PlayerHelper.healAndPrepPlayerForBattle(member);
				// Teleport before check-in to make sure they are at the arena already
				Gberry.safeTeleport(member, match.getWarpForPlayer(member));
				match.checkIn(member);
			}
		}

		for (UUID memberId : team2Members) {
			Player member = Bukkit.getPlayer(memberId);
			if (member != null) {
				if (member.isDead()) {
					member.spigot().respawn();
				}
				if (!TeamStateMachine.matchState.contains(member)) {
					State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(member);
					if (TeamStateMachine.spectatorState.contains(member)) {
						SpectatorHelper.disableSpectateGameMode(member);
					}
					if (TeamStateMachine.deathState.contains(member)) {
						DeathHelper.disableDeathMode(member);
					}
					try {
						currentState.transition(TeamStateMachine.matchState, member);
					} catch (IllegalStateTransitionException e) {
						e.printStackTrace();
					}
				}
				TeamStateMachine.matchState.setPlayerMatch(member, match);

				ArenaPvP.getInstance().setPlayerTeam(member, match.getPlayersTeam(member));

				PlayerHelper.healAndPrepPlayerForBattle(member);
				// Teleport before check-in to make sure they are at the arena already
				Gberry.safeTeleport(member, match.getWarpForPlayer(member));
				match.checkIn(member);
			}
		}

		JSONObject data = new JSONObject();
		data.put("server_name", Gberry.serverName);
		data.put("match_uuid", match.getMatchUuid().toString());
		data.put("team_1", team1IdStrings);
		data.put("team_2", team2IdStrings);
		List<JSONObject> currentSetupMatches = MCPListener.data.get("setup_matches");
		if (currentSetupMatches == null) {
			currentSetupMatches = new ArrayList<>();
		}
		currentSetupMatches.add(data);
		MCPListener.data.put("setup_matches", currentSetupMatches);
	}

	public static Map<UUID, Match> getMatchesAwaitingPlayers() {
		return matchesAwaitingPlayers;
	}

	public static Map<UUID, Team> getCombatLoggedPlayers() {
		return combatLoggedPlayers;
	}

	public static Map<Team, Match> getActiveMatches() {
		return activeMatches;
	}

	public static Map<UUID, Match> getRedRoverLoggedOutPlayers() {
		return redRoverLoggedOutPlayers;
	}
}
