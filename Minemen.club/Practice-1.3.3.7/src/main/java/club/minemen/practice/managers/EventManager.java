package club.minemen.practice.managers;

import club.minemen.practice.Practice;
import club.minemen.practice.events.EventState;
import club.minemen.practice.events.PracticeEvent;
import club.minemen.practice.events.sumo.SumoEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Getter
public class EventManager {
	private final Map<Class<? extends PracticeEvent>, PracticeEvent> events = new HashMap<>();

	private final Practice plugin = Practice.getInstance();

	private final World eventWorld;

	public EventManager() {
		Arrays.asList(
//				OITCEvent.class,
				SumoEvent.class
		).forEach(clazz -> this.addEvent(clazz));

		eventWorld = plugin.getServer().createWorld(new WorldCreator("event"));

		if (eventWorld != null) {
			plugin.getServer().getWorlds().add(eventWorld);

			eventWorld.setTime(2000L);
			eventWorld.setGameRuleValue("doDaylightCycle", "false");
			eventWorld.setGameRuleValue("doMobSpawning", "false");
			eventWorld.setStorm(false);
			eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
		}
	}

	public PracticeEvent getByName(String name) {
		return events.values().stream().filter(event -> event.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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

	public boolean isPlaying(Player player, PracticeEvent event) {
		return event.getPlayers().containsKey(player.getUniqueId());
	}

	public PracticeEvent getEventPlaying(Player player) {
		return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
	}
}