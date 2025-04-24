package club.minemen.practice.events.oitc;

import club.minemen.core.util.CustomLocation;
import club.minemen.practice.events.EventCountdownTask;
import club.minemen.practice.events.PracticeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Player;

public class OITCEvent extends PracticeEvent<OITCPlayer> {
	private final Map<UUID, OITCPlayer> players = new HashMap<>();
	private final EventCountdownTask countdownTask = new OITCCountdownTask(this);

	public OITCEvent() {
		super("OITC");
	}

	@Override
	public Map<UUID, OITCPlayer> getPlayers() {
		return players;
	}

	@Override
	public EventCountdownTask getCountdownTask() {
		return countdownTask;
	}

	@Override
	public List<CustomLocation> getSpawnLocations() {
		return null;
	}

	@Override
	public void onStart() {

	}

	@Override
	public Consumer<Player> onJoin() {
		return player -> players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
	}

	@Override
	public Consumer<Player> onDeath() {
		return player -> {

		};
	}
}
