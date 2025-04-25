package cc.patrone.practice.queue;

import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.tasks.QueueSearchTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class QueueEntry {
	@Getter
	private final PracticeProfile profile;
	@Getter
	private final Kit kit;
	@Getter
	private final boolean ranked;
	@Getter
	private final boolean party;
	@Getter
	private final int elo;
	@Setter
	private QueueSearchTask searchTask;

	public void cancelSearchTask() {
		if (searchTask != null) {
			searchTask.cancel();
		}
	}

	public int[] getCurrentRange() {
		return searchTask == null ? new int[]{elo - 50, elo + 50} : searchTask.getRange();
	}
}
