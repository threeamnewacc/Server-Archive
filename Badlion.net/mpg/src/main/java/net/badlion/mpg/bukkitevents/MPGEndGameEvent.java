package net.badlion.mpg.bukkitevents;

import net.badlion.mpg.MPGGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MPGEndGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private MPGGame mpgGame;

    public MPGEndGameEvent(MPGGame mpgGame) {
        this.mpgGame = mpgGame;
    }

    public MPGGame getMPGGame() {
        return mpgGame;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
