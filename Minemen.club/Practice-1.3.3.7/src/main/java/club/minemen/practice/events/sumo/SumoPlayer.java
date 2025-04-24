package club.minemen.practice.events.sumo;

import club.minemen.practice.events.EventPlayer;
import club.minemen.practice.events.PracticeEvent;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
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
		WAITING, FIGHTING, FOUGHT
	}
}
