package net.badlion.ministats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public interface MiniStatsPlayerCreator {

	public MiniStatsPlayer createMiniStatsPlayer(UUID uuid, ResultSet rs);

	public class DefaultMiniStatsPlayerCreator implements MiniStatsPlayerCreator {

		@Override
		public MiniStatsPlayer createMiniStatsPlayer(UUID uuid, ResultSet rs) {
			MiniStatsPlayer miniStatsPlayer = null;
			try {
				miniStatsPlayer = new MiniStatsPlayer(uuid, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return miniStatsPlayer;
		}

	}

}
