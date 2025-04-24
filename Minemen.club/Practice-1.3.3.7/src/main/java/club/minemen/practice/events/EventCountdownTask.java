package club.minemen.practice.events;

import club.minemen.core.clickable.Clickable;
import club.minemen.core.util.finalutil.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;

@Setter
@Getter
public abstract class EventCountdownTask extends BukkitRunnable {
	private final PracticeEvent event;
	private final int countdownTime;

	private int timeUntilStart;
	private boolean ended;

	public EventCountdownTask(PracticeEvent event, int countdownTime) {
		this.event = event;
		this.countdownTime = countdownTime;
		this.timeUntilStart = countdownTime;
	}

	@Override
	public void run() {
		if (isEnded()) {
			return;
		}

		if (timeUntilStart <= 0) {
			if (canStart()) {
				event.start();
			} else {
				onCancel();
			}

			ended = true;
			return;
		}

		if (shouldAnnounce(timeUntilStart)) {
			Clickable message = new Clickable(CC.B_GOLD + event.getName() + " is starting in " +
					getTime(timeUntilStart) + "! Click to join!",
					CC.GREEN + "Click to join!",
					"/joinevent " + event.getName());
			event.getPlugin().getServer().getOnlinePlayers().forEach(message::sendToPlayer);
		}

		timeUntilStart--;
	}

	public abstract boolean shouldAnnounce(int timeUntilStart);

	public abstract boolean canStart();

	public abstract void onCancel();

	/**
	 * Because TimeUtil#millisToRoundedTime is shit
	 */
	private String getTime(int time) {
		StringBuilder timeStr = new StringBuilder();
		int minutes = 0;

		if (time % 60 == 0) {
			minutes = time / 60;
			time = 0;
		} else {
			while (time - 60 > 0) {
				minutes++;
				time -= 60;
			}
		}

		if (minutes > 0) {
			timeStr.append(minutes).append("m");
		}
		if (time > 0) {
			timeStr.append(minutes > 0 ? " " : "").append(time).append("s");
		}

		return timeStr.toString();
	}
}
