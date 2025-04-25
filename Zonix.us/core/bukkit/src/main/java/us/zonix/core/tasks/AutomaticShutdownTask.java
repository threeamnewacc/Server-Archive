package us.zonix.core.tasks;

import us.zonix.core.CorePlugin;

import java.util.TimerTask;

public class AutomaticShutdownTask extends TimerTask {

	@Override
	public void run() {
		if (CorePlugin.getInstance().getShutdownTask() == null) {
			CorePlugin.getInstance().setShutdownTask(new ShutdownTask(CorePlugin.getInstance(), 60));
			CorePlugin.getInstance().getShutdownTask().runTaskTimer(CorePlugin.getInstance(), 20L, 20L);
		} else {
			CorePlugin.getInstance().getShutdownTask().setSecondsUntilShutdown(10);
		}
	}
}
