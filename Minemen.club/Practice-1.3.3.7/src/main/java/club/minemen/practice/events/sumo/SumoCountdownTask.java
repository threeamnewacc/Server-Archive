package club.minemen.practice.events.sumo;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.events.EventCountdownTask;
import club.minemen.practice.events.PracticeEvent;
import java.util.Arrays;

public class SumoCountdownTask extends EventCountdownTask {
	public SumoCountdownTask(PracticeEvent event) {
		super(event, 120);
	}

	@Override
	public boolean shouldAnnounce(int timeUntilStart) {
		return Arrays.asList(90, 60, 30, 15, 10, 5).contains(timeUntilStart);
	}

	@Override
	public boolean canStart() {
		return getEvent().getPlayers().size() >= 2;
	}

	@Override
	public void onCancel() {
		getEvent().sendMessage(CC.RED + "There were not enough players to start the event, so it has been canceled.");

		getEvent().end();
	}
}
