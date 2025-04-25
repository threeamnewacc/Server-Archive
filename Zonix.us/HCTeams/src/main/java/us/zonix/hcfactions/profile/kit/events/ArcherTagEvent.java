package us.zonix.hcfactions.profile.kit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArcherTagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player archer;
    private Player damaged;

    public ArcherTagEvent(Player archer, Player damaged) {
        this.archer = archer;
        this.damaged = damaged;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getArcher() {
        return this.archer;
    }

    public Player getDamaged() {
        return damaged;
    }

    public static HandlerList getHandlerList() {
        return handlers;

    }
}
