package club.minemen.events.event.impl.conquest;

import club.minemen.events.event.events.CaptureEvent;
import club.minemen.events.util.region.CuboidRegion;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ConquestPointEvent extends CaptureEvent {

	private transient ConquestEvent parent;

	public ConquestPointEvent() {
		super(null, null, TimeUnit.SECONDS, 30);
	}

	ConquestPointEvent(CuboidRegion capturePoint, ConquestEvent parent) {
		super(parent.getName(), capturePoint, TimeUnit.SECONDS, 30);

		this.parent = parent;
	}

	@Override
	public void onCapture(FactionPlayer factionPlayer) {
		this.parent.onCapture(factionPlayer);
	}

}
