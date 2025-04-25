package cc.patrone.practice.managers;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.leaderboard.Leaderboard;
import java.util.EnumMap;

public class LeaderboardManager {
	private final EnumMap<Kit, Leaderboard> leaderboards = new EnumMap<>(Kit.class);

	public LeaderboardManager(PracticePlugin plugin) {
		for (Kit kit : Kit.values()) {
			if (kit.isRanked()) {
				leaderboards.put(kit, new Leaderboard(plugin, kit.getName()));
			}
		}
	}

	public void updateALl() {
		for (Leaderboard leaderboard : leaderboards.values()) {
			leaderboard.update();
		}
	}

	public Leaderboard getLeaderboardByKit(Kit kit) {
		return leaderboards.get(kit);
	}
}
