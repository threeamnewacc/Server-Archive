package club.minemen.practice.request;

import club.minemen.core.api.request.Request;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;

@Builder
public class UpdateStatsRequest implements Request {

	private final UUID uuid;

	private final int nodebuffElo;
	private final int nodebuffEloParty;
	private final int nodebuffWins;
	private final int nodebuffLosses;

	private final int debuffElo;
	private final int debuffEloParty;
	private final int debuffWins;
	private final int debuffLosses;

	private final int classicElo;
	private final int classicEloParty;
	private final int classicWins;
	private final int classicLosses;

	private final int gappleElo;
	private final int gappleEloParty;
	private final int gappleWins;
	private final int gappleLosses;

	private final int archerElo;
	private final int archerEloParty;
	private final int archerWins;
	private final int archerLosses;

	private final int axeElo;
	private final int axeEloParty;
	private final int axeWins;
	private final int axeLosses;

	private final int hcfElo;
	private final int hcfEloParty;
	private final int hcfWins;
	private final int hcfLosses;

	private final int sumoElo;
	private final int sumoEloParty;
	private final int sumoWins;
	private final int sumoLosses;

	private final int builduhcElo;
	private final int builduhcEloParty;
	private final int builduhcWins;
	private final int builduhcLosses;

	private final int pingRange;
	private final int eloRange;
	private final int id;

	private final int premiumMatchesPlayed;
	private final int premiumMatchesExtra;
	private final int premiumLosses;
	private final int premiumWins;
	private final int premiumElo;

	private final boolean scoreboardEnabled;
	private final boolean acceptingDuels;
	private final boolean allowingSpectators;

	@Override
	public String getPath() {
		return "/practice/" + this.id + "/update";
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();

		map.put("allowing_spectators", this.allowingSpectators);
		map.put("scoreboard_enabled", this.scoreboardEnabled);
		map.put("accepting_duels", this.acceptingDuels);
		map.put("ping_range", this.pingRange);
		map.put("elo_range", this.eloRange);
		map.put("uuid", this.uuid.toString());
		map.put("id", this.id);

		map.put("nodebuff_elo_party", this.nodebuffEloParty);
		map.put("nodebuff_elo", this.nodebuffElo);
		map.put("nodebuff_wins", this.nodebuffWins);
		map.put("nodebuff_losses", this.nodebuffLosses);

		map.put("debuff_elo_party", this.debuffEloParty);
		map.put("debuff_losses", this.debuffLosses);
		map.put("debuff_wins", this.debuffWins);
		map.put("debuff_elo", this.debuffElo);

		map.put("gapple_elo_party", this.gappleEloParty);
		map.put("gapple_losses", this.gappleLosses);
		map.put("gapple_wins", this.gappleWins);
		map.put("gapple_elo", this.gappleElo);

		map.put("archer_elo_party", this.archerEloParty);
		map.put("archer_losses", this.archerLosses);
		map.put("archer_wins", this.archerWins);
		map.put("archer_elo", this.archerElo);

		map.put("classic_elo_party", this.classicEloParty);
		map.put("classic_losses", this.classicLosses);
		map.put("classic_wins", this.classicWins);
		map.put("classic_elo", this.classicElo);

		map.put("axe_elo_party", this.axeEloParty);
		map.put("axe_losses", this.axeLosses);
		map.put("axe_wins", this.axeWins);
		map.put("axe_elo", this.axeElo);

		map.put("soup_elo_party", this.axeEloParty);
		map.put("soup_losses", this.axeLosses);
		map.put("soup_wins", this.axeWins);
		map.put("soup_elo", this.axeElo);

		map.put("hcf_elo_party", this.hcfEloParty);
		map.put("hcf_losses", this.hcfLosses);
		map.put("hcf_wins", this.hcfWins);
		map.put("hcf_elo", this.hcfElo);

		map.put("sumo_elo_party", this.sumoEloParty);
		map.put("sumo_losses", this.sumoLosses);
		map.put("sumo_wins", this.sumoWins);
		map.put("sumo_elo", this.sumoElo);

		map.put("builduhc_elo_party", this.builduhcEloParty);
		map.put("builduhc_losses", this.builduhcLosses);
		map.put("builduhc_wins", this.builduhcWins);
		map.put("builduhc_elo", this.builduhcElo);

		map.put("premium_matches_played", this.premiumMatchesPlayed);
		map.put("premium_matches_extra", this.premiumMatchesExtra);
		map.put("premium_losses", this.premiumLosses);
		map.put("premium_wins", this.premiumWins);
		map.put("premium_elo", this.premiumElo);

		return map;
	}

}
