package net.badlion.potpvp.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerLeavePartyEvent  extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public PlayerLeavePartyEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
