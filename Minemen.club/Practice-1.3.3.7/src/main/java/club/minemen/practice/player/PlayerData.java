package club.minemen.practice.player;

import club.minemen.practice.Practice;
import club.minemen.practice.kit.PlayerKit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@RequiredArgsConstructor
public class PlayerData {

	public static final int DEFAULT_ELO = 1000;

	/*
	 * The maps don't need getters as they are never accessed directly.
	 */
	private final Map<String, Map<Integer, PlayerKit>> playerKits = new HashMap<>();
	private final Map<String, Integer> rankedLosses = new HashMap<>();
	private final Map<String, Integer> rankedWins = new HashMap<>();
	private final Map<String, Integer> rankedElo = new HashMap<>();
	private final Map<String, Integer> partyElo = new HashMap<>();

	@Getter
	private final UUID uniqueId;

	@Getter
	private PlayerState playerState = PlayerState.LOADING;

	@Getter
	private UUID currentMatchID;
	@Getter
	private UUID duelSelecting;

	@Getter
	private boolean acceptingDuels = true;
	@Getter
	private boolean allowingSpectators = true;
	@Getter
	private boolean scoreboardEnabled = true;

	@Getter
	private int cheatFreeMatches;
	@Getter
	private int minemanID = -1;
	@Getter
	private int eloRange = 250;
	@Getter
	private int pingRange = 50;
	@Getter
	private int teamID = -1;
	@Getter
	private int rematchID = -1;
	@Getter
	private int missedPots;
	@Getter
	private int longestCombo;
	@Getter
	private int combo;
	@Getter
	private int hits;

	@Getter
	private int premiumMatchesPlayed;
	@Getter
	private int premiumMatchesExtra;
	@Getter
	private int premiumLosses;
	@Getter
	private int premiumWins;
	@Getter
	private int premiumElo = PlayerData.DEFAULT_ELO;

	public int getPremiumMatches() {
		return Math.max(Practice.getInstance().getPlayerManager().getPremiumMatches(this.uniqueId)
				+ this.premiumMatchesExtra - this.premiumMatchesPlayed, 0);
	}

	public int getWins(String kitName) {
		return this.rankedWins.computeIfAbsent(kitName, k -> 0);
	}

	public void setWins(String kitName, int wins) {
		this.rankedWins.put(kitName, wins);
	}

	public int getLosses(String kitName) {
		return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
	}

	public void setLosses(String kitName, int losses) {
		this.rankedLosses.put(kitName, losses);
	}

	public int getElo(String kitName) {
		return this.rankedElo.computeIfAbsent(kitName, k -> PlayerData.DEFAULT_ELO);
	}

	public void setElo(String kitName, int elo) {
		this.rankedElo.put(kitName, elo);
	}

	public int getPartyElo(String kitName) {
		return this.partyElo.computeIfAbsent(kitName, k -> PlayerData.DEFAULT_ELO);
	}

	public void setPartyElo(String kitName, int elo) {
		this.partyElo.put(kitName, elo);
	}

	public void addPlayerKit(int index, PlayerKit playerKit) {
		this.getPlayerKits(playerKit.getName()).put(index, playerKit);
	}

	public Map<Integer, PlayerKit> getPlayerKits(String kitName) {
		return this.playerKits.computeIfAbsent(kitName, k -> new HashMap<>());
	}

}
