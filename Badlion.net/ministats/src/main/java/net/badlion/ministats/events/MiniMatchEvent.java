package net.badlion.ministats.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.simple.JSONObject;

public class MiniMatchEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private JSONObject matchJSON;

    public MiniMatchEvent(JSONObject matchJSON) {
        super(true);
        this.matchJSON = matchJSON;
    }

    public JSONObject getMatchJSON() {
        return matchJSON;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
