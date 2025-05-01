package net.badlion.ministats.events;

import net.badlion.ministats.MiniStatsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MiniPlayerStatsSaveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;

    private MiniStatsPlayer miniStatsPlayer;

    public MiniPlayerStatsSaveEvent(MiniStatsPlayer miniStatsPlayer) {
        super(true);

        this.miniStatsPlayer = miniStatsPlayer;
    }

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}

	public MiniStatsPlayer getMiniStatsPlayer() {
		return this.miniStatsPlayer;
	}

	public HandlerList getHandlers() {
        return MiniPlayerStatsSaveEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return MiniPlayerStatsSaveEvent.handlers;
    }

}
