package club.minemen.events.event;

import club.minemen.core.util.CustomLocation;
import club.minemen.events.event.impl.KothEvent;
import club.minemen.events.event.impl.conquest.ConquestEvent;
import club.minemen.events.event.impl.conquest.ConquestPointEvent;
import club.minemen.events.util.region.CuboidRegion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public final class EventManager {

	@Getter
	private final Map<String, Event> eventMap = new HashMap<>();

	public EventManager() {
//		this.register(new KothEvent("Test"));
//		this.register(new ConquestEvent("Conqwast", TimeUnit.SECONDS, 30L,
//				new CuboidRegion(CustomLocation.stringToLocation("-56, 64, 34, world"), CustomLocation.stringToLocation("-60, 64, 30, world")),
//				new CuboidRegion(CustomLocation.stringToLocation("-67, 64, 34, world"), CustomLocation.stringToLocation("-71, 64, 30, world")),
//				new CuboidRegion(CustomLocation.stringToLocation("-67, 64, 23, world"), CustomLocation.stringToLocation("-71, 64, 19, world")),
//				new CuboidRegion(CustomLocation.stringToLocation("-60, 64, 19, world"), CustomLocation.stringToLocation("-56, 64, 23, world"))
//		));


	}

	/**
	 * Not sure if this is an aids way to get them, but it allows us to use:
	 * <p>
	 * Set<CaptureEvent> events = EventManager#getActiveEvents();
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Event> List<T> getActiveEvents() {
		List<T> events = new ArrayList<>();

		for (Event event : this.eventMap.values()) {
			if (event.isRunning()) {
				try {
					events.addAll(event.getActiveEvents());
				} catch (ClassCastException ignore) {

				}
			}
		}

		return events;
	}

	public Event getEvent(String name) {
		return this.eventMap.get(name.toLowerCase());
	}

	public void register(Event event) {
		this.eventMap.put(event.getName().toLowerCase(), event);

		System.out.println(event.getName());
	}

}
