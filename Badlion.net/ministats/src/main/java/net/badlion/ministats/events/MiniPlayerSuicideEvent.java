package net.badlion.ministats.events;

import net.badlion.ministats.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.simple.JSONObject;

public class MiniPlayerSuicideEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private PlayerData playerData;
    private JSONObject jsonObject;

    public MiniPlayerSuicideEvent(PlayerData playerData, JSONObject jsonObject) {
        super(true);
        this.playerData = playerData;
        this.jsonObject = jsonObject;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
