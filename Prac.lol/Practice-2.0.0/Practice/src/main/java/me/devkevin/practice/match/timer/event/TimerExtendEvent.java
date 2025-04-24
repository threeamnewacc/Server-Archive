package me.devkevin.practice.match.timer.event;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.devkevin.practice.match.timer.Timer;

public class TimerExtendEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Optional<Player> player;
	private final Optional<UUID> userUUID;
	private final Timer timer;
	private final long previousDuration;
	private boolean cancelled;
	private long newDuration;

	public TimerExtendEvent(Timer timer, long previousDuration, long newDuration) {
		this.player = Optional.empty();
		this.userUUID = Optional.empty();
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
	}

	public TimerExtendEvent(@Nullable Player player, UUID uniqueId, Timer timer, long previousDuration,
	                        long newDuration) {
		this.player = Optional.ofNullable(player);
		this.userUUID = Optional.ofNullable(uniqueId);
		this.timer = timer;
		this.previousDuration = previousDuration;
		this.newDuration = newDuration;
	}

	public static HandlerList getHandlerList() {
		return TimerExtendEvent.HANDLERS;
	}

	public Optional<Player> getPlayer() {
		return this.player;
	}

	public Optional<UUID> getUserUUID() {
		return this.userUUID;
	}

	public Timer getTimer() {
		return this.timer;
	}

	public long getPreviousDuration() {
		return this.previousDuration;
	}

	public long getNewDuration() {
		return this.newDuration;
	}

	public void setNewDuration(long newDuration) {
		this.newDuration = newDuration;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return TimerExtendEvent.HANDLERS;
	}
}
