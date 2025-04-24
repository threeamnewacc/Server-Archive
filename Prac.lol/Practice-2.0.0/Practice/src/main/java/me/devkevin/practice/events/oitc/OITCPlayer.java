package me.devkevin.practice.events.oitc;

import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;
import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OITCPlayer extends EventPlayer {

	private OITCState state = OITCState.WAITING;
	private int score = 0;
	private int lives = 5;
	private BukkitTask respawnTask;
	private OITCPlayer lastKiller;

	public OITCPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum OITCState {
		WAITING, PREPARING, FIGHTING, RESPAWNING, ELIMINATED
	}
}
