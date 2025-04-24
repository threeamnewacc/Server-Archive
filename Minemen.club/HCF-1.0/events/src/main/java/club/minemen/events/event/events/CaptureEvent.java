package club.minemen.events.event.events;

import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.events.EventsPlugin;
import club.minemen.events.event.Event;
import club.minemen.events.task.CaptureTimerTask;
import club.minemen.events.util.region.CuboidRegion;
import com.massivecraft.factions.FactionPlayer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
public abstract class CaptureEvent implements Event<FactionPlayer> {

	private final transient Set<UUID> capturingPlayers = new HashSet<>();
	private String name;
	private long captureTime;

	private CuboidRegion cuboidRegion;

	private transient CaptureTimerTask timerTask;

	private transient UUID capturing;

	private transient boolean contested;
	private transient boolean knocked;

	private transient boolean running;

	private transient long captureStartTime;
	private transient long startTime;

	public CaptureEvent(String name, CuboidRegion cuboidRegion, TimeUnit timeUnit, long time) {
		this.name = name;
		this.cuboidRegion = cuboidRegion;

		this.captureTime = timeUnit.toMillis(time);
	}

	@Override
	public void onStart() {
		this.running = true;
		this.timerTask = new CaptureTimerTask(EventsPlugin.getInstance(), this);
		this.timerTask.runTaskTimer(EventsPlugin.getInstance(), 0L, 10L);

		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void onEnd(FactionPlayer winner) {
		this.running = false;
		if (this.timerTask != null) {
			this.timerTask.cancel();
		}
		this.capturing = null;
		this.capturingPlayers.clear();
		this.captureStartTime = 0;
	}

	/**
	 * By default it's first faction to capture the objective wins.
	 * Other events can override this (e.g. conquest)
	 *
	 * @param factionPlayer - Player who captured the point
	 */
	@SuppressWarnings("unchecked")
	public void onCapture(FactionPlayer factionPlayer) {
		this.onEnd(factionPlayer);
	}

	public boolean isCapturing(Player player) {
		return this.capturingPlayers.contains(player.getUniqueId());
	}

	public boolean isInside(Player player) {
		return this.isInside(player.getLocation());
	}

	public boolean isInside(Location location) {
		if (this.cuboidRegion == null) {
			return false;
		}

		return this.cuboidRegion.contains(CustomLocation.fromBukkitLocation(location));
	}

	public String getTimeRemainingDisplay() {
		return DurationFormatUtils.formatDuration(this.capturing == null ? this.getCaptureTime() :
		                                          (this.getCaptureTime() + this.getCaptureStartTime()) -
		                                          System.currentTimeMillis(), "mm:ss");
	}

	@Override
	public String getScoreboardDisplay() {
		return CC.B_BLUE + this.getName() + ": " + CC.RESET + this.getTimeRemainingDisplay();
	}

	@Override
	public boolean canStart() {
		return this.cuboidRegion != null;
	}

}
