package us.zonix.hcfactions.profile.kit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArcherTagRemoveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    public ArcherTagRemoveEvent(Player player) {
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return handlers;

    }
}
