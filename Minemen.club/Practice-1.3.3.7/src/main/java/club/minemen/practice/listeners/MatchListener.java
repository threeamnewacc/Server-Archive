package club.minemen.practice.listeners;

import club.minemen.core.clickable.Clickable;
import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.event.match.MatchEndEvent;
import club.minemen.practice.event.match.MatchStartEvent;
import club.minemen.practice.inventory.InventorySnapshot;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchState;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.queue.QueueType;
import club.minemen.practice.runnable.MatchRunnable;
import club.minemen.practice.util.EloUtil;
import club.minemen.practice.util.PlayerUtil;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MatchListener implements Listener {

	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		Match match = event.getMatch();
		Kit kit = match.getKit();

		if (!kit.isEnabled()) {
			match.broadcast(CC.RED + "This kit is currently disabled, try another kit.");
			this.plugin.getMatchManager().removeMatch(match);
			return;
		}

		if (kit.isBuild() || kit.isSpleef()) {
			if (match.getArena().getAvailableArenas().size() > 0) {
				match.setStandaloneArena(match.getArena().getAvailableArena());
				this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
			} else {
				match.broadcast(CC.RED + "There are no arenas available.");
				this.plugin.getMatchManager().removeMatch(match);
				return;
			}
		}

		Set<Player> matchPlayers = new HashSet<>();

		match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> {
			matchPlayers.add(player);

			this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());

			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

			player.setAllowFlight(false);
			player.setFlying(false);

			playerData.setCurrentMatchID(match.getMatchId());
			playerData.setTeamID(team.getTeamID());

			playerData.setMissedPots(0);
			playerData.setLongestCombo(0);
			playerData.setCombo(0);
			playerData.setHits(0);

			PlayerUtil.clearPlayer(player);

			CustomLocation locationA = match.getStandaloneArena() != null ? match.getStandaloneArena().getA() : match.getArena().getA();
			CustomLocation locationB = match.getStandaloneArena() != null ? match.getStandaloneArena().getB() : match.getArena().getB();
			player.teleport(team.getTeamID() == 1 ? locationA.toBukkitLocation() : locationB.toBukkitLocation());
			if (kit.isCombo()) {
				player.setMaximumNoDamageTicks(3);
			}
			if (!match.isRedrover()) {
				this.plugin.getMatchManager().giveKits(player, kit);

				playerData.setPlayerState(PlayerState.FIGHTING);
			} else {
				this.plugin.getMatchManager().addRedroverSpectator(player, match);
			}
		}));

		for (Player player : matchPlayers) {
			for (Player online : this.plugin.getServer().getOnlinePlayers()) {
				online.hidePlayer(player);
				player.hidePlayer(online);
			}
		}

		for (Player player : matchPlayers) {
			for (Player other : matchPlayers) {
				player.showPlayer(other);
			}
		}

		new MatchRunnable(match).runTaskTimer(this.plugin, 20L, 20L);
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		Clickable inventories = new Clickable(CC.PRIMARY + "Inventories: ");

		match.setMatchState(MatchState.ENDING);
		match.setWinningTeamId(event.getWinningTeam().getTeamID());
		match.setCountdown(4);

		if (match.isFFA()) {
			Player winner = this.plugin.getServer().getPlayer(event.getWinningTeam().getAlivePlayers().get(0));
			String winnerMessage = CC.PRIMARY + "Winner: " + CC.SECONDARY + winner.getName();

			event.getWinningTeam().players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}
				inventories.add((player.getUniqueId() == winner.getUniqueId() ? CC.GREEN : CC.RED)
								+ player.getName() + " ",
						CC.PRIMARY + "View Inventory",
						"/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
			});
			for (InventorySnapshot snapshot : match.getSnapshots().values()) {
				this.plugin.getInventoryManager().addSnapshot(snapshot);
			}

			match.broadcast(winnerMessage);
			match.broadcast(inventories);
		} else if (match.isRedrover()) {
			match.broadcast(CC.SECONDARY + event.getWinningTeam().getLeaderName() + CC.PRIMARY + " has won the redrover!");
		} else {
			Map<UUID, InventorySnapshot> inventorySnapshotMap = new LinkedHashMap<>();
			match.getTeams().forEach(team -> team.players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}

				inventorySnapshotMap
						.put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));

				boolean onWinningTeam =
						this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getTeamID() ==
								event.getWinningTeam().getTeamID();
				inventories.add((onWinningTeam ? CC.GREEN : CC.RED)
								+ player.getName() + " ",
						CC.PRIMARY + "View inventory",
						"/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
			}));
			for (InventorySnapshot snapshot : match.getSnapshots().values()) {
				this.plugin.getInventoryManager().addSnapshot(snapshot);
			}

			String winnerMessage = CC.PRIMARY + (match.isParty() ? "Winning Team: " : "Winner: ")
					+ CC.SECONDARY + event.getWinningTeam().getLeaderName();

			match.broadcast(winnerMessage);
			match.broadcast(inventories);

			if (match.getType().isRanked()) {
				String kitName = match.getKit().getName();

				Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
				PlayerData winnerLeaderData = this.plugin.getPlayerManager()
						.getPlayerData(winnerLeader.getUniqueId());
				Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
				PlayerData loserLeaderData = this.plugin.getPlayerManager()
						.getPlayerData(loserLeader.getUniqueId());

				String eloMessage;

				int[] preElo = new int[2];
				int[] newElo = new int[2];
				int winnerElo;
				int loserElo;
				int newWinnerElo;
				int newLoserElo;

				if (event.getWinningTeam().getPlayers().size() == 2) {
					Player winnerMember = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(1));
					PlayerData winnerMemberData = this.plugin.getPlayerManager().getPlayerData(winnerMember.getUniqueId());

					Player loserMember = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(1));
					PlayerData loserMemberData = this.plugin.getPlayerManager().getPlayerData(loserMember.getUniqueId());

					winnerElo = winnerLeaderData.getPartyElo(kitName);
					loserElo = loserLeaderData.getPartyElo(kitName);

					preElo[0] = winnerElo;
					preElo[1] = loserElo;

					newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
					newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

					newElo[0] = newWinnerElo;
					newElo[1] = newLoserElo;

					winnerMemberData.setPartyElo(kitName, newWinnerElo);
					loserMemberData.setPartyElo(kitName, newLoserElo);

					eloMessage = CC.AQUA + "Updated Elo: " + CC.GREEN + winnerLeader.getName() + ", " +
							winnerMember.getName() + " " + newWinnerElo +
							" (+" + (newWinnerElo - winnerElo) + ") " + CC.RED + loserLeader.getName() + "," +
							" " +
							loserMember.getName() + " " +
							newLoserElo + " (" + (newLoserElo - loserElo) + ")";
				} else {
					if (match.getType() == QueueType.RANKED) {
						winnerElo = winnerLeaderData.getElo(kitName);
						loserElo = loserLeaderData.getElo(kitName);
					} else {
						winnerElo = winnerLeaderData.getPremiumElo();
						loserElo = loserLeaderData.getPremiumElo();
					}

					preElo[0] = winnerElo;
					preElo[1] = loserElo;

					newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
					newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

					newElo[0] = newWinnerElo;
					newElo[1] = newLoserElo;

					eloMessage = CC.AQUA + "Updated Elo: " + CC.GREEN + winnerLeader.getName() + " " + newWinnerElo +
							" (+" + (newWinnerElo - winnerElo) + ") " +
							CC.RED + loserLeader.getName() + " " + newLoserElo + " (" +
							(newLoserElo - loserElo) + ")";

					if (match.getType() == QueueType.RANKED) {
						winnerLeaderData.setElo(kitName, newWinnerElo);
						loserLeaderData.setElo(kitName, newLoserElo);

						winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
						loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
					} else {
						winnerLeaderData.setPremiumElo(newWinnerElo);
						loserLeaderData.setPremiumElo(newLoserElo);

						winnerLeaderData.setPremiumWins(winnerLeaderData.getPremiumWins() + 1);
						loserLeaderData.setPremiumLosses(loserLeaderData.getPremiumLosses() + 1);
					}
				}

				match.broadcast(eloMessage);

				/*
				// Match data (only for 1v1's atm)
				if (event.getWinningTeam().getPlayers().size() == 1) {
					//					match.broadcast(CC.PRIMARY + "View a summary of your match here: " + CC
					// .SECONDARY +
					//					                "https://minemen.club/match/practice/" + match.getMatchId());

					InventorySnapshot snapshotA = inventorySnapshotMap.get(event.getWinningTeam().getLeader());
					InventorySnapshot snapshotB = inventorySnapshotMap.get(event.getLosingTeam().getLeader());
					InventorySnapshotRequest inventorySnapshotRequest = new InventorySnapshotRequest(snapshotA
							.toJson(), snapshotB.toJson(), match.getMatchId());
					CorePlugin.getInstance().getRequestManager().sendRequest(inventorySnapshotRequest,
							new AbstractCallback("Error inserting inventory data " + match.getMatchId()) {
								@Override
								public void callback(JSONObject data) {
									String response = (String) data.get("response");
									if (response.equals("success")) {
										int inventoryId = ((Long) data.get("id")).intValue();

										List<Integer> losers = event.getLosingTeam().getPlayerIds();
										List<Integer> winners = event.getWinningTeam().getPlayerIds();

										InsertMatchRequest request = new InsertMatchRequest(match.getMatchId(),
												winners.get(0), losers.get(0),
												inventoryId, preElo, newElo);

										CorePlugin.getInstance().getRequestManager().sendRequest(request,
												new AbstractCallback(
														"Error inserting match " + match.getMatchId().toString()) {
													@Override
													public void callback(JSONObject data) {
														String response = (String) data.get("response");
														if (!response.equals("success")) {
															MatchListener.this.plugin.getServer().getLogger().warning(
																	"Server-sided error when saving match data. " +
																			data.toJSONString());
														}
													}
												});
									}
								}
							});

				}
				*/
			}
			this.plugin.getMatchManager().saveRematches(match);
		}
	}
}
