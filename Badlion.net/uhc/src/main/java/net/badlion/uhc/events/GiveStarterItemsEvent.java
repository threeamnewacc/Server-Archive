package net.badlion.uhc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class GiveStarterItemsEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public GiveStarterItemsEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
