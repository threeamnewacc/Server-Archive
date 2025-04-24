package me.devkevin.practice.events.manager;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventState;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.lms.LMSEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.events.tntrun.TntRunEvent;
import me.devkevin.practice.events.tnttag.TNTTagEvent;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class EventManager {
	private final Map<Class<? extends PracticeEvent>, PracticeEvent> events = new HashMap<>();

	private final Practice plugin = Practice.getInstance();

	private List<UUID> spectators;

	@Setter private long cooldown;
	private final World eventWorld;

	public EventManager() {
		Arrays.asList(
				SumoEvent.class,
				OITCEvent.class,
				TNTTagEvent.class,
				TntRunEvent.class,
				LMSEvent.class
		).forEach(clazz -> this.addEvent(clazz));

		boolean newWorld;
		eventWorld = plugin.getServer().getWorld("event");
		newWorld = false;

		this.spectators = new ArrayList<>();

		this.cooldown = 0L;

		if (eventWorld != null) {

			if (newWorld) {
				plugin.getServer().getWorlds().add(eventWorld);
			}

			eventWorld.setTime(2000L);
			eventWorld.setGameRuleValue("doDaylightCycle", "false");
			eventWorld.setGameRuleValue("doMobSpawning", "false");
			eventWorld.setStorm(false);
			eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
		}
	}

	public PracticeEvent getByName(String name) {
		return events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
	}

	public void addSpectatorLMS(Player player, Profile profile, LMSEvent event) {
		if(player.isDead()) {
			player.setHealth(20.0);
		}

		this.addSpectator(player, profile, event);

		List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
		player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());

		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.ADVENTURE);

		player.setAllowFlight(true);
		player.setFlying(true);
	}


	public void hostEvent(PracticeEvent event, Player host) {

		event.setState(EventState.WAITING);
		event.setHost(host);
		event.startCountdown();
	}

	private void addEvent(Class<? extends PracticeEvent> clazz) {
		PracticeEvent event = null;

		try {
			event = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		events.put(clazz, event);
	}

	public void addSpectatorSumo(Player player, Profile playerData, SumoEvent event) {

		this.addSpectator(player, playerData, event);

		player.teleport(Practice.getInstance().getCustomLocationManager().getSumoLocation().toBukkitLocation());

		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}
	}

	public void addSpectatorOITC(Player player, Profile playerData, OITCEvent event) {

		this.addSpectator(player, playerData, event);

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}
	}

	public void addSpectator(Player player, Profile playerData, PracticeEvent event) {


		this.spectators.add(player.getUniqueId());

		playerData.setState(ProfileState.SPECTATING);

		player.setAllowFlight(true);
		player.setFlying(true);

		player.getInventory().setContents(this.plugin.getHotbarItem().getSpecItems());
		player.updateInventory();

		this.plugin.getServer().getOnlinePlayers().forEach(online -> {
			online.hidePlayer(player);
			player.hidePlayer(online);
		});

	}

	public void addSpectatorTNTTag(Player player, Profile profile, TNTTagEvent event) {

		this.addSpectator(player, profile, event);

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.SPECTATOR);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addDeathSpectatorTntTag(Player player, Profile profile, TNTTagEvent event) {

		this.plugin.getServer().getOnlinePlayers().forEach(online -> {
			online.hidePlayer(player);
			player.hidePlayer(online);
		});

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.SURVIVAL);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addSpectatorTNTRun(Player player, Profile profile, TntRunEvent event) {

		this.addSpectator(player, profile, event);

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.SURVIVAL);

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	public void addDeathSpectatorTntRun(Player player, Profile profile, TntRunEvent event) {

		this.plugin.getServer().getOnlinePlayers().forEach(online -> {
			online.hidePlayer(player);
			player.hidePlayer(online);
		});

		if (event.getSpawnLocations().size() == 1) {
			player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
		} else {
			List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
			player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
		}


		for(Player eventPlayer : event.getBukkitPlayers()) {
			player.showPlayer(eventPlayer);
		}

		player.setGameMode(GameMode.SURVIVAL);

		player.setAllowFlight(true);
		player.setFlying(true);
	}


	private void showPlayers() {

	}

	public void removeSpectator(Player player) {
		this.getSpectators().remove(player.getUniqueId());
		this.plugin.getProfileManager().sendToSpawn(player);
	}


	public boolean isPlaying(Player player, PracticeEvent event) {
		return event.getPlayers().containsKey(player.getUniqueId());
	}

	public PracticeEvent getEventPlaying(Player player) {
		return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
	}
}