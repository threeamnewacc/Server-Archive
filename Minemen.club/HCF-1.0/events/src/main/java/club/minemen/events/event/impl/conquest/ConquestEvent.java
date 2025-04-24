package club.minemen.events.event.impl.conquest;

import club.minemen.events.event.Event;
import club.minemen.events.event.events.CaptureEvent;
import club.minemen.events.util.region.CuboidRegion;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionPlayer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class ConquestEvent extends CaptureEvent {

	private final transient Map<Faction, Integer> pointMap = new HashMap<>();

	private final Set<ConquestPointEvent> capturePoints = new HashSet<>();

	public ConquestEvent() {
		super(null, null, TimeUnit.MICROSECONDS, 1);
	}

	public ConquestEvent(String name, TimeUnit timeUnit, long time, CuboidRegion... regions) {
		super(name, null, timeUnit, time);

		for (CuboidRegion region : regions) {
			this.capturePoints.add(new ConquestPointEvent(region, this));
		}
	}

	@Override
	public void onStart() {
		this.setRunning(true);

		this.setStartTime(System.currentTimeMillis());

		for (ConquestPointEvent point : this.capturePoints) {
			point.onStart();
		}
	}

	@Override
	public void onCapture(FactionPlayer factionPlayer) {
		Faction faction = factionPlayer.getFaction();

		int points = this.pointMap.compute(faction, (f, i) -> i == null ? 1 : ++i);
		if (points > 120) {
			this.onEnd(factionPlayer);
		}
	}

	@Override
	public boolean canStart() {
		for (ConquestPointEvent capturePoint : this.capturePoints) {
			if (capturePoint.getCuboidRegion() == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<Event> getActiveEvents() {
		Set<Event> events = new HashSet<>(this.capturePoints);
		return events;
	}
}
