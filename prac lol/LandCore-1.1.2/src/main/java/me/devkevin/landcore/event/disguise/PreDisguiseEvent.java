package me.devkevin.landcore.event.disguise;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 19/01/2023 @ 17:19
 * PreDisguiseEvent / land.pvp.core.event.disguise / LandCore
 */
@Getter
@RequiredArgsConstructor
public class PreDisguiseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS  = new HandlerList();

    private final Player player;

    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

