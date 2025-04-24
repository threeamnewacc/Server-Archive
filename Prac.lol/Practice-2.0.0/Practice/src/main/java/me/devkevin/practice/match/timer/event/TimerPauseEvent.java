package me.devkevin.practice.match.timer.event;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.devkevin.practice.match.timer.Timer;

public class TimerPauseEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private final boolean paused;
	private final Optional<UUID> userUUID;
	private final Timer timer;
	private boolean cancelled;

	public TimerPauseEvent(Timer timer, boolean paused) {
		this.userUUID = Optional.empty();
		this.timer = timer;
		this.paused = paused;
	}

	public TimerPauseEvent(UUID userUUID, Timer timer, boolean paused) {
		this.userUUID = Optional.ofNullable(userUUID);
		this.timer = timer;
		this.paused = paused;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Optional<UUID> getUserUUID() {
		return this.userUUID;
	}

	public Timer getTimer() {
		return this.timer;
	}

	public boolean isPaused() {
		return this.paused;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
