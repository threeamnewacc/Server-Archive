package net.badlion.ministats.events;

import net.badlion.ministats.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MiniPlayerKillEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private PlayerData.PlayerKill playerKill;

    public MiniPlayerKillEvent(PlayerData.PlayerKill playerKill) {
        super(true);
        this.playerKill = playerKill;
    }

    public PlayerData.PlayerKill getPlayerKill() {
        return playerKill;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
