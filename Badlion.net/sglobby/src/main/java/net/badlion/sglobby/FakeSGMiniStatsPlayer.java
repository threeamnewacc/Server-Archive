package net.badlion.sglobby;

import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.ministats.MiniStatsPlayerCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeSGMiniStatsPlayer extends MiniStatsPlayer {

	public static class FakeSGMiniStatsPlayerCreator implements MiniStatsPlayerCreator {

		@Override
		public MiniStatsPlayer createMiniStatsPlayer(UUID uuid, ResultSet rs) {
			FakeSGMiniStatsPlayer fakeSGMiniStatsPlayer;
			try {
				fakeSGMiniStatsPlayer = new FakeSGMiniStatsPlayer(uuid, rs);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			return fakeSGMiniStatsPlayer;
		}

	}

	private int supplyDropsOpened = 0;

	private Map<Integer, Integer> tierChestsOpened = new HashMap<>();

	public FakeSGMiniStatsPlayer(UUID uuid, ResultSet rs) throws SQLException {
		super(uuid, rs);

		if (rs != null) {
			this.tierChestsOpened.put(1, rs.getInt("tier1_opened"));
			this.tierChestsOpened.put(2, rs.getInt("tier2_opened"));

			this.supplyDropsOpened =  rs.getInt("supply_drops_opened");
		} else {
			this.tierChestsOpened.put(1, 0);
			this.tierChestsOpened.put(2, 0);
		}
	}

	public int getNumberOfGamesPlayed() {
		return this.getWins() + this.getLosses();
	}

	public int getNumberOfTierChestsOpened(int tier) {
		return this.tierChestsOpened.get(tier);
	}

	public int getNumberOfSupplyDropsOpened() {
		return this.supplyDropsOpened;
	}

}
