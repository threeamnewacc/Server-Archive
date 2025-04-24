package club.minemen.practice.events.sumo;

import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.events.EventCountdownTask;
import club.minemen.practice.events.PracticeEvent;
import club.minemen.practice.util.PlayerUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SumoEvent extends PracticeEvent<SumoPlayer> {
	private final Map<UUID, SumoPlayer> players = new HashMap<>();
	private final SumoCountdownTask countdownTask = new SumoCountdownTask(this);

	public SumoEvent() {
		super("Sumo");

		new WaterCheckTask().runTaskTimer(getPlugin(), 0, 20);
	}

	@Override
	public Map<UUID, SumoPlayer> getPlayers() {
		return players;
	}

	@Override
	public EventCountdownTask getCountdownTask() {
		return countdownTask;
	}

	@Override
	public List<CustomLocation> getSpawnLocations() {
		return Collections.singletonList(
				new CustomLocation(getPlugin().getEventManager().getEventWorld().getName(), 18.5, 18.5, 0.5, 90, 0)
		);
	}

	@Override
	public void onStart() {
		selectPlayers();
	}

	@Override
	public Consumer<Player> onJoin() {
		return player -> players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId(), this));
	}

	@Override
	public Consumer<Player> onDeath() {
		return player -> {
			SumoPlayer data = getPlayer(player);

			if (data.getState() != SumoPlayer.SumoState.FIGHTING || data.getFighting() == null) {
				return;
			}

			getPlayers().remove(player.getUniqueId());

			SumoPlayer killerData = data.getFighting();
			Player killer = getPlugin().getServer().getPlayer(killerData.getUuid());

			data.getFightTask().cancel();
			killerData.getFightTask().cancel();

			sendMessage(CC.GREEN + killer.getName() + " won against " + player.getName() + "!");

			if (getPlayers().size() == 1) {
				Player winner = getPlugin().getServer().getPlayer((UUID) getPlayers().keySet().toArray()[0]);

				getPlugin().getServer().broadcastMessage(CC.B_GOLD + winner.getName() + " won Sumo!");

				end();
			} else {
				getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> selectPlayers(), 3 * 20);
			}
		};
	}

	private CustomLocation[] getSumoLocations() {
		CustomLocation[] array = new CustomLocation[2];
		array[0] = new CustomLocation(getPlugin().getEventManager().getEventWorld().getName(), 4.5, 18, 0.5, 90, 0);
		array[1] = new CustomLocation(getPlugin().getEventManager().getEventWorld().getName(), -3.5, 18, 0.5, -90, 0);
		return array;
	}

	private void selectPlayers() {
		if (getByState(SumoPlayer.SumoState.WAITING).size() < 2) {
			players.values().forEach(player -> player.setState(SumoPlayer.SumoState.WAITING));
		}

		Player picked1 = getRandomPlayer();
		Player picked2 = getRandomPlayer();

		SumoPlayer picked1Data = getPlayer(picked1);
		SumoPlayer picked2Data = getPlayer(picked2);

		picked1Data.setFighting(picked2Data);
		picked2Data.setFighting(picked1Data);

		picked1.teleport(getSumoLocations()[0].toBukkitLocation());
		picked2.teleport(getSumoLocations()[1].toBukkitLocation());

		sendMessage(CC.YELLOW + picked1.getName() + " VS " + picked2.getName());

		BukkitTask task = new SumoFightTask(picked1, picked2).runTaskTimer(getPlugin(), 0, 20);

		picked1Data.setFightTask(task);
		picked2Data.setFightTask(task);
	}

	private Player getRandomPlayer() {
		List<UUID> waiting = getByState(SumoPlayer.SumoState.WAITING);

		Collections.shuffle(waiting);

		UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));

		SumoPlayer data = getPlayer(uuid);
		data.setState(SumoPlayer.SumoState.FIGHTING);

		return getPlugin().getServer().getPlayer(uuid);
	}

	private List<UUID> getByState(SumoPlayer.SumoState state) {
		return players.values().stream().filter(player -> player.getState() == state).map(SumoPlayer::getUuid).collect(Collectors.toList());
	}

	/**
	 * To ensure that the fight doesn't go on forever and to
	 * let the players know how much time they have left.
	 */
	@RequiredArgsConstructor
	private class SumoFightTask extends BukkitRunnable {
		private final Player player;
		private final Player other;
		private int time = 60;

		@Override
		public void run() {
			// Make sure we don't get a fuck ton of NPEs
			if (player == null || other == null || !player.isOnline() || !other.isOnline()) {
				cancel();
				return;
			}

			if (time == 60) {
				PlayerUtil.sendMessage(CC.D_RED + "3...", player, other);
			} else if (time == 59) {
				PlayerUtil.sendMessage(CC.RED + "2...", player, other);
			} else if (time == 58) {
				PlayerUtil.sendMessage(CC.YELLOW + "1...", player, other);
			} else if (time == 57) {
				PlayerUtil.sendMessage(CC.GREEN + "Fight!", player, other);
			} else if (time <= 0) {
				List<Player> players = Arrays.asList(player, other);
				Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));

				players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> onDeath().accept(pl));

				cancel();
				return;
			}

			if (Arrays.asList(45, 30, 15, 10).contains(time)) {
				PlayerUtil.sendMessage(CC.GOLD + "Fight ends in " + time + " seconds.", player, other);
			} else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
				PlayerUtil.sendMessage(CC.GOLD + "A winner will be automatically selected in " + time + " seconds.", player, other);
			}

			time--;
		}
	}

	@RequiredArgsConstructor
	private class WaterCheckTask extends BukkitRunnable {
		@Override
		public void run() {
			if (getPlayers().size() <= 1) {
				return;
			}

			getBukkitPlayers().forEach(player -> {
				if (getPlayer(player).getState() != SumoPlayer.SumoState.FIGHTING) {
					return;
				}

				Block legs = player.getLocation().getBlock();
				Block head = legs.getRelative(BlockFace.UP);
				if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
					onDeath().accept(player);
				}
			});
		}
	}
}
