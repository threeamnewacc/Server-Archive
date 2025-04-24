package club.minemen.events.util;

import club.minemen.core.CorePlugin;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.TimeUtil;
import club.minemen.events.event.events.CaptureEvent;

public final class MessageUtil {

	private static String format(CaptureEvent event, String message) {
		String startTime = TimeUtil.millisToRoundedTime(System.currentTimeMillis() - event.getStartTime());

		return message.replace("%time%", event.getTimeRemainingDisplay())
				.replace("%start-time%", startTime).replace("%event%", event.getName());
	}

	public static void broadcast(CaptureEvent event, String message) {
		String messageFormatted = format(event, message);

		String formatted = String.format("%s[%s] %s", CC.GOLD, event.getName(), messageFormatted);

		CorePlugin.getInstance().getServer().broadcastMessage(formatted);
	}

}
