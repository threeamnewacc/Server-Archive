package net.badlion.uhc.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class UHCTeleportPlayerLocationEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private Location location;

    public UHCTeleportPlayerLocationEvent(Player player) {
        super(player);
    }

    public UHCTeleportPlayerLocationEvent(Player player, Location location) {
        super(player);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
