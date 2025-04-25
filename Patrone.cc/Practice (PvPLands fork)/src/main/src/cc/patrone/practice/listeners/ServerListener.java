package cc.patrone.practice.listeners;

import zone.potion.event.server.ServerShutdownCancelEvent;
import zone.potion.event.server.ServerShutdownScheduleEvent;
import cc.patrone.practice.PracticePlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class ServerListener implements Listener {
	private final PracticePlugin plugin;

	@EventHandler
	public void onShutdownSchedule(ServerShutdownScheduleEvent event) {
		plugin.getQueueManager().setRankedEnabled(false);
	}

	@EventHandler
	public void onShutdownSchedule(ServerShutdownCancelEvent event) {
		plugin.getQueueManager().setRankedEnabled(true);
	}
}
