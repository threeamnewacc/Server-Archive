package me.devkevin.practice.events;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.event.EventStartEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.oitc.OITCPlayer;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.events.sumo.SumoPlayer;
import me.devkevin.practice.events.tnttag.TNTTagEvent;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class PracticeEvent<K extends EventPlayer> {
	private final Practice plugin = Practice.getInstance();

	private final String name;

	private int limit = 30;
	private Player host;
	private EventState state = EventState.UNANNOUNCED;

	public void startCountdown() {
		// Restart Logic
		if (getCountdownTask().isEnded()) {
			getCountdownTask().setTimeUntilStart(getCountdownTask().getCountdownTime());
			getCountdownTask().setEnded(false);
		} else {
			getCountdownTask().runTaskTimerAsynchronously(plugin, 20L, 20L);
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

		if(this.getPlayers().size() >= this.limit) {
			return;
		}

		Profile playerData = plugin.getProfileManager().getProfileData(player.getUniqueId());
		playerData.setState(ProfileState.EVENT);

		PlayerUtil.reset(player);

		if (onJoin() != null) {
			onJoin().accept(player);
		}

		if (this.getSpawnLocations().size() == 1) {
			player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			final List<CustomLocation> spawnLocations = new ArrayList<>(this.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}

		this.plugin.getProfileManager().giveSpawnItems(player);

		for(Player other : this.getBukkitPlayers()) {
			other.showPlayer(player);
			player.showPlayer(other);
		}


		this.sendMessage(ChatColor.YELLOW + player.getName() + " has joined the event. (" + this.getPlayers().size() + " player" + (this.getPlayers().size() == 1 ? "": "s") + ")");
	}

	public void leave(Player player) {

		if(this instanceof OITCEvent) {
			OITCEvent oitcEvent = (OITCEvent) this;
			OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
			oitcPlayer.setState(OITCPlayer.OITCState.ELIMINATED);
		}

		if (onDeath() != null) {
			onDeath().accept(player);
		}

		getPlayers().remove(player.getUniqueId());

		plugin.getProfileManager().sendToSpawn(player);
	}

	public void start() {

		new EventStartEvent(this).call();

		setState(EventState.STARTED);

		onStart();

		this.plugin.getEventManager().setCooldown(0L);
	}

	public void end() {
		setState(EventState.UNANNOUNCED);

		Bukkit.getOnlinePlayers().forEach(player -> {
			Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
			if (profile.getState() == ProfileState.EVENT) {
				this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
						Bukkit.getWorld(this.getSpawnLocations().get(0).getWorld()).getPlayers().forEach(dsada ->
								this.plugin.getProfileManager().sendToSpawn(player)), 2L);
			}
		});

		this.plugin.getEventManager().setCooldown(System.currentTimeMillis() + (60 * 5) * 1000L);

		if (this instanceof SumoEvent) {

			SumoEvent sumoEvent = (SumoEvent) this;
			for(SumoPlayer sumoPlayer : sumoEvent.getPlayers().values()) {

				if(sumoPlayer.getFightTask() != null) {
					sumoPlayer.getFightTask().cancel();
				}
			}
		} else if (this instanceof TNTTagEvent) {
			TNTTagEvent tntTagEvent = (TNTTagEvent)this;

			if (tntTagEvent.getTntTagTask() != null) {
				tntTagEvent.getTntTagTask().cancel();
				tntTagEvent.setTntTagTask(null);
			}
		}

		getPlayers().clear();

		Iterator<UUID> iterator = this.plugin.getEventManager().getSpectators().iterator();

		while(iterator.hasNext()) {
			UUID spectatorUUID = iterator.next();
			Player spectator = Bukkit.getPlayer(spectatorUUID);

			if(spectator != null) {
				this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getProfileManager().sendToSpawn(spectator));
				iterator.remove();
			}
		}
		this.getBukkitPlayers().forEach(player -> this.plugin.getProfileManager().sendToSpawn(player));
		this.plugin.getEventManager().getSpectators().clear();
		this.getPlayers().clear();
		getCountdownTask().setEnded(true);
	}

	public void handleWin(Player winner) {
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage( winner.getDisplayName() + CC.GREEN + " has won the " + CC.GREEN + CC.BOLD + name + CC.GREEN + " event!");
		Bukkit.broadcastMessage("");
	}

	public boolean isWaiting() {
		return state == EventState.WAITING;
	}

	public boolean isEnd() {
		return state == EventState.UNANNOUNCED;
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
