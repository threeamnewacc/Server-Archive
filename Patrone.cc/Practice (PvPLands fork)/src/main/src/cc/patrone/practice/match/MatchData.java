package cc.patrone.practice.match;

import cc.patrone.practice.utils.timer.PearlTimer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class MatchData {
	private final Match match;
	private final int teamId;
	private final PearlTimer pearlTimer = new PearlTimer();
	@Setter
	private boolean playerInMatch = true;
	private int hits;
	private int missedPots;
	private int combo;
	private int longestCombo;

	public void incrementHits() {
		hits++;
	}

	public void incrementMissedPots() {
		missedPots++;
	}

	public void resetCombo() {
		if (combo > longestCombo) {
			longestCombo = combo;
		}

		combo = 0;
	}

	public void incrementCombo() {
		combo++;

		if (combo > longestCombo) {
			longestCombo = combo;
		}
	}
}
