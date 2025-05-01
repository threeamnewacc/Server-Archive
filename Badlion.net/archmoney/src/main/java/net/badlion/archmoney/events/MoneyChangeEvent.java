package net.badlion.archmoney.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MoneyChangeEvent extends Event {

    private Player player;
    private String factionID;

    private static final HandlerList handlers = new HandlerList();

    public MoneyChangeEvent(Player player, String factionID) {
        this.player = player;
        this.factionID = factionID;
    }

    public Player getPlayer() {
        return player;
    }

    public String getFactionID() {
        return factionID;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}