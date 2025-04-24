package club.minemen.events.task;

import club.minemen.events.EventsPlugin;
import club.minemen.events.event.events.CaptureEvent;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import static club.minemen.events.util.MessageUtil.broadcast;

@RequiredArgsConstructor
public final class CaptureTimerTask extends BukkitRunnable {

	private final EventsPlugin plugin;
	private final CaptureEvent event;

	private boolean capturing;
	private boolean knocked;

	@Override
	public void run() {
		this.event.onTick();

		if (this.event.getCapturing() == null) {
			this.capturing = false;

			if (!this.knocked) {
				broadcast(this.event, "No one is capturing " + this.event.getName() + "!");
				this.knocked = true;
			}
			return;
		} else {
			this.knocked = false;
		}

		FactionPlayer factionPlayer = FPlayers.getInstance().getById(this.event.getCapturing().toString());

		if (System.currentTimeMillis() >= this.event.getCaptureStartTime() + this.event.getCaptureTime()) {
			broadcast(this.event, factionPlayer.getName() + " captured %event% in %start-time%!");

			this.event.onCapture(factionPlayer);

			this.capturing = this.knocked = false;
			return;
		}

		if (!this.capturing || (System.currentTimeMillis() - this.event.getCaptureStartTime()) / 1000L % 60 == 0) {
			broadcast(this.event, "Someone is capturing %event%! [%time%]");
			this.capturing = true;
		}
	}

}
