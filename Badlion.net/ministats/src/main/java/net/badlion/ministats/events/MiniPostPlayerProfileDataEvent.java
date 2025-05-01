package net.badlion.ministats.events;

import net.badlion.ministats.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.simple.JSONObject;

public class MiniPostPlayerProfileDataEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private PlayerData playerData;
    private JSONObject profileJSON;

    public MiniPostPlayerProfileDataEvent(PlayerData playerData, JSONObject profileJSON) {
        super(true);
        this.playerData = playerData;
        this.profileJSON = profileJSON;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public JSONObject getProfileJSON() {
        return profileJSON;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
