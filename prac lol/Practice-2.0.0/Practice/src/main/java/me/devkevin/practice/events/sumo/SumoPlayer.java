package me.devkevin.practice.events.sumo;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;
import org.bukkit.scheduler.BukkitTask;

@Setter
@Getter
public class SumoPlayer extends EventPlayer {

	private SumoState state = SumoState.WAITING;
	private BukkitTask fightTask;
	private SumoPlayer fighting;

	public SumoPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum SumoState {
		WAITING, PREPARING, FIGHTING, ELIMINATED
	}
}
