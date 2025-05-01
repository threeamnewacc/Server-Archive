package net.badlion.gberry.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;

public class SettingsLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Map<String, String> settings;

    public SettingsLoadedEvent(Map<String, String> settings) {
        this.settings = settings;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
