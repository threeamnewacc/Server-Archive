package cc.patrone.practice.managers;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.match.Match;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MatchManager {
	private final List<Match> ongoingMatches = new ArrayList<>();
	private final PracticePlugin plugin;

    private final Object matchesLock = new Object();

	public int playersFighting(String kitName, boolean rankedMatch) {
		int playersFighting = 0;

		synchronized (matchesLock) {
			for (Match match : ongoingMatches) {
				if (match.getKit().getName().equals(kitName) && match.isRanked() == rankedMatch) {
					playersFighting += match.playersFighting();
				}
			}
		}

		return playersFighting;
	}

	public int playersFighting() {
		int playersFighting = 0;


		synchronized (matchesLock) {
			for (Match match : ongoingMatches) {
				playersFighting += match.playersFighting();
			}
		}

		return playersFighting;
	}

	public void startMatch(Match match) {
		synchronized (matchesLock) {
			ongoingMatches.add(match);
		}
		match.start(plugin);
	}

	public void removeMatch(Match match) {
		synchronized (matchesLock) {
			ongoingMatches.remove(match);
		}
	}
}
