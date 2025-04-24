package club.minemen.practice.events.oitc;

import club.minemen.practice.events.EventPlayer;
import club.minemen.practice.events.PracticeEvent;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OITCPlayer extends EventPlayer {
	private int score = 0;

	public OITCPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}
}
