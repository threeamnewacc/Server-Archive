package club.minemen.events.handler;

import club.minemen.events.EventsPlugin;
import club.minemen.events.event.events.CaptureEvent;
import club.minemen.spigot.handler.MovementHandler;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class EventMovementHandler implements MovementHandler {

	private final EventsPlugin plugin;

	@Override public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packet) {
		if (to.getBlockZ() == from.getBlockZ() && to.getBlockX() == from.getBlockX()) {
			return;
		}

		List<CaptureEvent> events = this.plugin.getEventManager().getActiveEvents();

		for (CaptureEvent event : events) {
			boolean capturing = event.isInside(player);

			if (capturing) {
				if (event.getCapturing() == null) {
					event.setCaptureStartTime(System.currentTimeMillis());
					event.setCapturing(player.getUniqueId());
				}

				event.getCapturingPlayers().add(player.getUniqueId());
				break;
			} else {
				if (event.getCapturing() == player.getUniqueId()) {
					event.setCaptureStartTime(0);
					event.setCapturing(null);
				}

				event.getCapturingPlayers().remove(player.getUniqueId());
			}
		}
	}

	@Override public void handleUpdateRotation(Player player, Location to, Location from, PacketPlayInFlying packet) {
		// N/A
	}


}
