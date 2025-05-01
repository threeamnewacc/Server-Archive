package net.badlion.mpg.bukkitevents;

import net.badlion.mpg.MPGWorld;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class MapManagerInitializeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private List<MPGWorld> worlds = new ArrayList<>();

    public List<MPGWorld> getWorlds() {
        return worlds;
    }

    public void addMPGWorld(MPGWorld mpgWorld) {
        this.worlds.add(mpgWorld);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
