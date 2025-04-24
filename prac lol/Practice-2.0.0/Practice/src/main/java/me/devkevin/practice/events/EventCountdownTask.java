package me.devkevin.practice.events;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.util.Clickable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

				Bukkit.getScheduler().runTask(Practice.getInstance(), () -> event.start());
			} else {
				Bukkit.getScheduler().runTask(Practice.getInstance(), () -> onCancel());
			}

			ended = true;
			return;
		}

		if (shouldAnnounce(timeUntilStart)) {

			CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(event.getHost().getUniqueId());

			String toSend = "";
			String toSendDonor = "";

			if(profile == null) {
				toSend = ChatColor.GREEN + "" + event.getName() + " is starting soon. " + ChatColor.GRAY + "[Click to Join]";
			} else {
				toSend =  ChatColor.GREEN + "" + event.getName() + " is starting soon. " + ChatColor.GRAY + "[Click to Join]";
				toSendDonor = ChatColor.GRAY + "[" + profile.getGrant().getRank().getColor() + ChatColor.BOLD + "*" + ChatColor.GRAY + "] " + profile.getGrant().getRank().getColor() + ChatColor.BOLD + event.getHost().getName() + ChatColor.WHITE + " is hosting a " + profile.getGrant().getRank().getColor() + ChatColor.BOLD + event.getName() + " Event. " + ChatColor.GRAY + "[Click to Join]";
			}

			Clickable message = new Clickable(profile != null && profile.hasDonor() ? toSendDonor : toSend,
					ChatColor.GRAY + "Click to join this event.",
					"/join " + event.getName());
			Bukkit.getServer().getOnlinePlayers().forEach(message::sendToPlayer);

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
