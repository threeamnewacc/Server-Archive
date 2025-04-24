package me.devkevin.practice.events.sumo;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SumoEvent extends PracticeEvent<SumoPlayer> {

	private final Map<UUID, SumoPlayer> players = new HashMap<>();

	@Getter final HashSet<String> fighting = new HashSet<>();
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
		return Collections.singletonList(this.getPlugin().getCustomLocationManager().getSumoLocation());
	}

	@Override
	public void onStart() {
		for(UUID playerUUID : players.keySet()) {

			Player player = Bukkit.getPlayer(playerUUID);

			if(player != null) {
				PlayerUtil.reset(player);
			}
		}
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

			if (data == null || data.getFighting() == null) {
				return;
			}

			if(data.getState() == SumoPlayer.SumoState.FIGHTING || data.getState() == SumoPlayer.SumoState.PREPARING) {

				SumoPlayer killerData = data.getFighting();
				Player killer = getPlugin().getServer().getPlayer(killerData.getUuid());

				data.getFightTask().cancel();
				killerData.getFightTask().cancel();


				Profile playerData = this.getPlugin().getProfileManager().getProfileData(player.getUniqueId());

				if (playerData != null) {
					playerData.setSumoLosses(playerData.getSumoLosses() + 1);
				}

				data.setState(SumoPlayer.SumoState.ELIMINATED);
				killerData.setState(SumoPlayer.SumoState.WAITING);

				PlayerUtil.reset(player);
				this.getPlugin().getProfileManager().giveSpawnItems(player);

				PlayerUtil.reset(killer);
				this.getPlugin().getProfileManager().giveSpawnItems(killer);

				if (getSpawnLocations().size() == 1) {
					player.teleport(getSpawnLocations().get(0).toBukkitLocation());
					killer.teleport(getSpawnLocations().get(0).toBukkitLocation());
				}

				sendMessage(LandCore.getInstance().getProfileManager().getProfile(killer.getUniqueId()).getGrant().getRank().getColor() + killer.getName() + CC.GREEN + " won against " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + "!");
				player.sendMessage("");
				player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
				if (!player.hasPermission("practice.donors.gold")) {
					player.sendMessage(CC.GRAY + "Purchase a rank at https://udrop.buycraft.net/ to host events of your own.");
				}
				player.sendMessage(" ");

				if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
					Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));

					Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(winner.getUniqueId());
					winnerData.setSumoWins(winnerData.getSumoWins() + 1);

					handleWin(killer);

					this.fighting.clear();
					end();
				} else {
					getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> selectPlayers(), 3 * 20);
				}
			}
		};
	}

	private CustomLocation[] getSumoLocations() {
		CustomLocation[] array = new CustomLocation[2];
		array[0] = this.getPlugin().getCustomLocationManager().getSumoFirst();
		array[1] = this.getPlugin().getCustomLocationManager().getSumoSecond();
		return array;
	}

	private void selectPlayers() {

		if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
			Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));

			Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(winner.getUniqueId());
			winnerData.setSumoWins(winnerData.getSumoWins() + 1);

			this.fighting.clear();
			end();
			return;
		}

		sendMessage(CC.GOLD + CC.BOLD + "(Sumo) " + ChatColor.GRAY + CC.I + "Selecting random players...");

		this.fighting.clear();

		//if (getByState(SumoPlayer.SumoState.WAITING).size() < 2) {
		//	players.values().forEach(player -> player.setState(SumoPlayer.SumoState.WAITING));
		//}

		Player picked1 = getRandomPlayer();
		Player picked2 = getRandomPlayer();

		SumoPlayer picked1Data = getPlayer(picked1);
		SumoPlayer picked2Data = getPlayer(picked2);

		picked1Data.setFighting(picked2Data);
		picked2Data.setFighting(picked1Data);

		this.fighting.add(picked1.getName());
		this.fighting.add(picked2.getName());

		PlayerUtil.reset(picked1);
		PlayerUtil.reset(picked2);

		picked1.teleport(getSumoLocations()[0].toBukkitLocation());
		picked2.teleport(getSumoLocations()[1].toBukkitLocation());

		for(Player other : this.getBukkitPlayers()) {
			if(other != null) {
				other.showPlayer(picked1);
				other.showPlayer(picked2);
			}
		}

		for(UUID spectatorUUID : this.getPlugin().getEventManager().getSpectators()) {
			Player spectator = Bukkit.getPlayer(spectatorUUID);
			if(spectatorUUID != null) {
				spectator.showPlayer(picked1);
				spectator.showPlayer(picked2);
			}
		}

		picked1.showPlayer(picked2);
		picked2.showPlayer(picked1);

		sendMessage(LandCore.getInstance().getProfileManager().getProfile(picked1.getUniqueId()).getGrant().getRank().getColor() + picked1.getName() + CC.GREEN + " VS " + LandCore.getInstance().getProfileManager().getProfile(picked2.getUniqueId()).getGrant().getRank().getColor() + picked2.getName());

		BukkitTask task = new SumoFightTask(picked1, picked2, picked1Data, picked2Data).runTaskTimer(getPlugin(), 0, 20);

		picked1Data.setFightTask(task);
		picked2Data.setFightTask(task);
	}

	private Player getRandomPlayer() {

		if(getByState(SumoPlayer.SumoState.WAITING).size() == 0) {
			return null;
		}


		List<UUID> waiting = getByState(SumoPlayer.SumoState.WAITING);

		Collections.shuffle(waiting);

		UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));

		SumoPlayer data = getPlayer(uuid);
		data.setState(SumoPlayer.SumoState.PREPARING);

		return getPlugin().getServer().getPlayer(uuid);
	}

	public List<UUID> getByState(SumoPlayer.SumoState state) {
		return players.values().stream().filter(player -> player.getState() == state).map(SumoPlayer::getUuid).collect(Collectors.toList());
	}

	/**
	 * To ensure that the fight doesn't go on forever and to
	 * let the players know how much time they have left.
	 */
	@Getter
	@RequiredArgsConstructor
	public class SumoFightTask extends BukkitRunnable {
		private final Player player;
		private final Player other;

		private final SumoPlayer playerSumo;
		private final SumoPlayer otherSumo;

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
				this.otherSumo.setState(SumoPlayer.SumoState.FIGHTING);
				this.playerSumo.setState(SumoPlayer.SumoState.FIGHTING);
			} else if (time <= 0) {
				List<Player> players = Arrays.asList(player, other);
				Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
				players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> onDeath().accept(pl));

				cancel();
				return;
			}

			if (Arrays.asList(30, 25, 20, 15, 10).contains(time)) {
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

				if (getPlayer(player) != null && getPlayer(player).getState() != SumoPlayer.SumoState.FIGHTING) {
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
