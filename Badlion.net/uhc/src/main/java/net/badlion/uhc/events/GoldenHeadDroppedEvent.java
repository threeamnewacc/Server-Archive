package net.badlion.uhc.events;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GoldenHeadDroppedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;
    private LivingEntity entity;
    private Location headLocation;

    public GoldenHeadDroppedEvent(LivingEntity entity, Location location) {
        super();

        this.entity = entity;
        this.headLocation = location;
    }

    public Location getHeadLocation() {
        return headLocation;
    }

    public void setHeadLocation(Location headLocation) {
        this.headLocation = headLocation;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

}
