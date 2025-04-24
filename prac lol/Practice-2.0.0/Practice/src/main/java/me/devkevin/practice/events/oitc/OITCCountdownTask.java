package me.devkevin.practice.events.oitc;

import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;
import org.bukkit.ChatColor;
import java.util.Arrays;

public class OITCCountdownTask extends EventCountdownTask {

	public OITCCountdownTask(PracticeEvent event) {
		super(event, 60);
	}

	@Override
	public boolean shouldAnnounce(int timeUntilStart) {
		return Arrays.asList(45, 30, 15, 10, 5).contains(timeUntilStart);
	}

	@Override
	public boolean canStart() {
		return getEvent().getPlayers().size() >= 2;
	}

	@Override
	public void onCancel() {
		getEvent().sendMessage(ChatColor.RED + "Not enough players. Event has been cancelled");
		getEvent().end();
		this.getEvent().getPlugin().getEventManager().setCooldown(0L);
	}
}
