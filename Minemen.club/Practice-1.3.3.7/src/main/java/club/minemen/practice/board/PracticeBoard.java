package club.minemen.practice.board;

import club.minemen.core.CorePlugin;
import club.minemen.core.board.Board;
import club.minemen.core.board.BoardAdapter;
import club.minemen.core.mineman.Mineman;
import club.minemen.core.rank.Rank;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.party.Party;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.queue.QueueEntry;
import club.minemen.practice.queue.QueueType;
import club.minemen.practice.tournament.Tournament;
import club.minemen.practice.util.MathUtil;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PracticeBoard implements BoardAdapter {

	private final Practice plugin = Practice.getInstance();
	private final String footer;

	private int fighters;

	public PracticeBoard() {
		this.footer = this.plugin.getMainConfig().getConfig().getString("footer");
	}

	@Override
	public String getTitle(Player player) {
		return CC.BD_PURPLE + "PRACTICE";
	}

	@Override
	public void preLoop() {
		this.fighters = this.plugin.getMatchManager().getFighters();
	}

	@Override
	public List<String> getScoreboard(Player player, Board board) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData == null) {
			this.plugin.getLogger().warning(player.getName() + "'s player data is null");
			return null;
		}

		if (!playerData.isScoreboardEnabled()) {
			return null;
		}

		switch (playerData.getPlayerState()) {
			case LOADING:
			case EDITING:
			case FFA:
			case SPAWN:
			case SPECTATING:
				return this.getLobbyBoard(player, false);
			case QUEUE:
				return this.getLobbyBoard(player, true);
			case FIGHTING:
				return this.getGameBoard(player);
		}

		return null;
	}

	private List<String> getLobbyBoard(Player player, boolean queuing) {
		List<String> strings = new LinkedList<>();
		strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");

		strings.add(CC.RESET + "Online: " + CC.PRIMARY + this.plugin.getServer().getOnlinePlayers().size());
		strings.add(CC.RESET + "Playing: " + CC.PRIMARY + this.fighters);

		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());

		if (tournament != null) {
			strings.add(CC.RED + " ");
			strings.add(CC.RESET + "Tournament:");
			strings.add(CC.PRIMARY + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName());
			strings.add(CC.RESET + "Round: " + CC.PRIMARY + tournament.getCurrentRound());
			strings.add(CC.RESET + "Players: " + CC.PRIMARY
					+ tournament.getPlayers().size() + "/" + CC.PRIMARY + tournament.getSize());
		} else if (party != null) {
			strings.add(CC.RED + " ");
			strings.add(CC.RESET + "Party:");
			strings.add(CC.RESET + "Leader: " + CC.PRIMARY
					+ this.plugin.getServer().getPlayer(party.getLeader()).getName());
			strings.add(CC.RESET + "Members: " + CC.PRIMARY + party.getMembers().size());
		}

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (queuing) {
			strings.add(CC.RED + " ");
			QueueEntry queueEntry = party == null ? this.plugin.getQueueManager().getQueueEntry(player.getUniqueId()) :
					this.plugin.getQueueManager().getQueueEntry(party.getLeader());

			strings.add(CC.RESET + "Queue:");
			strings.add(CC.PRIMARY + queueEntry.getQueueType().getName() + " " + queueEntry.getKitName());

			if (queueEntry.getQueueType() != QueueType.UNRANKED) {
				Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

				long queueTime = System.currentTimeMillis() -
						(party == null ? this.plugin.getQueueManager().getPlayerQueueTime(player
								.getUniqueId())
								: this.plugin.getQueueManager().getPlayerQueueTime(party.getLeader()));

				int eloRange = mineman.hasRank(Rank.CLUBBER) ? playerData.getEloRange() : -1;
				int seconds = Math.round(queueTime / 1000L);
				if (seconds > 5) {
					if (eloRange != -1) {
						eloRange += seconds * 50;
						if (eloRange >= 3000) {
							eloRange = 3000;
						}
					}
				}

				int elo;
				if (queueEntry.getQueueType() == QueueType.RANKED) {
					elo = playerData.getElo(queueEntry.getKitName());
				} else {
					elo = playerData.getPremiumElo();
				}

				strings.add(CC.RESET + "ELO range:");
				strings.add(CC.PRIMARY + (eloRange == -1 ? "Unrestricted" :
						"[" + Math.max(elo - eloRange / 2, 0) +
								" -> " + Math.max(elo + eloRange / 2, 0) + "]"));
			}
		} else {
			strings.add(CC.RESET + " ");

			int maxMatches = this.plugin.getPlayerManager().getPremiumMatches(player.getUniqueId());
			if (maxMatches == 1337) {
				strings.add(CC.RESET + "Premium Matches:");
				strings.add(CC.PRIMARY + "Unlimited");
			} else {
				strings.add(CC.RESET + "Premium Matches: " + CC.PRIMARY + playerData.getPremiumMatches());
			}
		}

		strings.add(CC.RED + " ");
		strings.add(CC.PRIMARY + (this.footer != null ? this.footer : "minemen.club"));
		strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");

		return strings;
	}

	@Override
	public void onScoreboardCreate(Player player, Scoreboard scoreboard) {
		Team red = scoreboard.getTeam("red");

		if (red == null) {
			red = scoreboard.registerNewTeam("red");
		}
		Team green = scoreboard.getTeam("green");

		if (green == null) {
			green = scoreboard.registerNewTeam("green");
		}

		red.setPrefix(CC.RED);
		green.setPrefix(CC.GREEN);

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.FIGHTING) {
			for (String entry : red.getEntries()) {
				red.removeEntry(entry);
			}
			for (String entry : green.getEntries()) {
				green.removeEntry(entry);
			}
			return;
		}

		Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

		for (MatchTeam team : match.getTeams()) {
			for (UUID teamUUID : team.getAlivePlayers()) {
				Player teamPlayer = this.plugin.getServer().getPlayer(teamUUID);
				if (teamPlayer != null) {
					String teamPlayerName = teamPlayer.getName();
					if (team.getTeamID() == playerData.getTeamID() && !match.isFFA()) {
						if (!green.hasEntry(teamPlayerName)) {
							green.addEntry(teamPlayerName);
						}
					} else {
						if (!red.hasEntry(teamPlayerName)) {
							red.addEntry(teamPlayerName);
						}
					}
				}
			}
		}
	}

	private List<String> getGameBoard(Player player) {
		List<String> strings = new LinkedList<>();
		strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");

		Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

		strings.add(CC.RESET + "Ladder: " + CC.PRIMARY + match.getKit().getName());

		Player opponentPlayer = null;
		if (!match.isParty() && !match.isFFA()) {
			opponentPlayer = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()
					? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0))
					: this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
			if (opponentPlayer == null) {
				return this.getLobbyBoard(player, false);
			}

			strings.add(CC.RESET + "Opponent: " + CC.PRIMARY + opponentPlayer.getName());
		} else if (match.isParty() && !match.isFFA()) {
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

			MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) :
					(playerData.getTeamID() == 0 ? match.getTeams().get(1) : match.getTeams().get(0));
			MatchTeam playerTeam = match.getTeams().get(playerData.getTeamID());
			//This is how we know it's a 2v2 or not
			if (opposingTeam.getPlayers().size() == 2 && playerTeam.getPlayers().size() == 2) {
				Player teammate = this.plugin.getServer().getPlayer(
						playerTeam.getPlayers().get(0) == player.getUniqueId() ? playerTeam.getPlayers().get(1) :
								playerTeam.getPlayers().get(0));
				strings.add(CC.GOLD + " ");
				strings.add(CC.RESET + "Teammates:");
				if (teammate != null) {
					if (playerTeam.getAlivePlayers().contains(teammate.getUniqueId())) {
						strings.add(CC.RESET + " " + teammate.getName() + CC.PRIMARY + " (" +
								MathUtil.roundToHalves(teammate.getHealth() / 2.0D) + " ❤)");

						boolean potionMatch = false;
						boolean soupMatch = false;

						for (ItemStack item : match.getKit().getContents()) {
							if (item == null) {
								continue;
							}
							if (item.getType() == Material.MUSHROOM_SOUP) {
								soupMatch = true;
								break;
							} else if (item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
								potionMatch = true;
								break;
							}
						}

						if (potionMatch) {
							int potCount = (int) Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull)
									.map(ItemStack::getDurability).filter(d -> d == 16421).count();

							strings.add(" " + CC.PRIMARY + potCount + " pots");
						} else if (soupMatch) {
							int soupCount = (int) Arrays.stream(teammate.getInventory().getContents()).filter(Objects::nonNull).map(ItemStack::getType)
									.filter(d -> d == Material.MUSHROOM_SOUP).count();

							strings.add(" " + CC.PRIMARY + soupCount + " soups");
						}
					} else {
						strings.add(CC.RESET + " " + teammate.getName() + CC.D_RED + " (✘)");
					}
				}
				if (opposingTeam.getAlivePlayers().size() > 0) {
					strings.add(CC.RESET + "Opponents: ");
					for (UUID opponent : opposingTeam.getAlivePlayers()) {
						strings.add(CC.PRIMARY + " " + this.plugin.getServer().getPlayer(opponent).getName());
					}
				}
			} else {
				//Not a 2v2, don't fuck up the board just show the amount of opponents
				strings.add(CC.RESET + "Teammates: " + CC.PRIMARY + playerTeam.getAlivePlayers().size());
				strings.add(CC.RESET + "Opponents: " + CC.PRIMARY + opposingTeam.getAlivePlayers().size());
			}
		} else if (match.isFFA()) {
			strings.add(CC.RESET + "Opponents: " + CC.PRIMARY + (match.getTeams().get(0).getAlivePlayers().size() - 1));
		}

		strings.add(CC.RED + " ");
//		strings.add(CC.RESET + "Your Ping: " + CC.PRIMARY + AGCAPI.getPing(player) + " ms");
//		if (opponentPlayer != null) {
//			strings.add(CC.RESET + "Their Ping: " + CC.PRIMARY + AGCAPI.getPing(opponentPlayer) + " ms");
//		}

		strings.add(CC.GOLD + " ");
		strings.add(CC.PRIMARY + this.footer);
		strings.add(CC.GRAY + CC.STRIKE_THROUGH + "-------------------");

		return strings;
	}

	@Override
	public long getInterval() {
		return 20L;
	}

}
