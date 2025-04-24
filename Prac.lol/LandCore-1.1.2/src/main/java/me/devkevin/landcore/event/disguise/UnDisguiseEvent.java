package me.devkevin.landcore.event.disguise;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 19/01/2023 @ 17:20
 * UnDisguiseEvent / land.pvp.core.event.disguise / LandCore
 */
@Getter
@RequiredArgsConstructor
public class UnDisguiseEvent extends Event {

    private static final HandlerList HANDLERS  = new HandlerList();

    private final Player player;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
