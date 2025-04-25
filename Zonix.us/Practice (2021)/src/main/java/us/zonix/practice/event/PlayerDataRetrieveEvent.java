package us.zonix.practice.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.zonix.practice.player.PlayerData;

public class PlayerDataRetrieveEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final PlayerData playerData;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    public PlayerDataRetrieveEvent(PlayerData playerData) {
        this.playerData = playerData;
    }
}
