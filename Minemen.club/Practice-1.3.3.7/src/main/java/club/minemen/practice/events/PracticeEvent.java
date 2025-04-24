package club.minemen.practice.events;

import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.event.EventStartEvent;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.util.PlayerUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class PracticeEvent<K extends EventPlayer> {
	private final Practice plugin = Practice.getInstance();

	private final String name;

	private Player host;
	private EventState state = EventState.UNANNOUNCED;

	public void startCountdown() {
		// Restart Logic
		if (getCountdownTask().isEnded()) {
			getCountdownTask().setTimeUntilStart(getCountdownTask().getCountdownTime());
			getCountdownTask().setEnded(false);
		} else {
			getCountdownTask().runTaskTimer(plugin, 20L, 20L);
		}
	}

	public void sendMessage(String message) {
		getBukkitPlayers().forEach(player -> player.sendMessage(message));
	}

	public Set<Player> getBukkitPlayers() {
		return getPlayers().keySet().stream()
				.filter(uuid -> plugin.getServer().getPlayer(uuid) != null)
				.map(plugin.getServer()::getPlayer)
				.collect(Collectors.toSet());
	}

	public void join(Player player) {
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		playerData.setPlayerState(PlayerState.EVENT);

		PlayerUtil.clearPlayer(player);

		if (onJoin() != null) {
			onJoin().accept(player);
		}

		if (getSpawnLocations().size() == 1) {
			player.teleport(getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}

		getBukkitPlayers().forEach(other -> other.showPlayer(player));
		getBukkitPlayers().forEach(other -> player.showPlayer(other));

		player.sendMessage(CC.SECONDARY + "You are now playing " + CC.PRIMARY + name + CC.SECONDARY + ".");
	}

	public void leave(Player player, boolean disconnect) {
		getPlayers().remove(player.getUniqueId());

		if (!disconnect) {
			plugin.getPlayerManager().sendToSpawnAndReset(player);

			if (onDeath() != null) {
				onDeath().accept(player);
			}
		}
	}

	public void start() {
		new EventStartEvent(this).call();

		setState(EventState.STARTED);

		onStart();
	}

	public void end() {
		plugin.getEventManager().getEventWorld().getPlayers().forEach(player -> plugin.getPlayerManager().sendToSpawnAndReset(player));

		getPlayers().clear();

		setState(EventState.UNANNOUNCED);
	}

	public K getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	public K getPlayer(UUID uuid) {
		return getPlayers().get(uuid);
	}

	public abstract Map<UUID, K> getPlayers();

	public abstract EventCountdownTask getCountdownTask();

	public abstract List<CustomLocation> getSpawnLocations();

	public abstract void onStart();

	public abstract Consumer<Player> onJoin();

	public abstract Consumer<Player> onDeath();
}
