package net.badlion.gfactions.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HomeChangeEvent extends Event {

    private Player p;
    private static final HandlerList handlers = new HandlerList();

    public HomeChangeEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
