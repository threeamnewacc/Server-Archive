package me.devkevin.landcore.event.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlayerMessageEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player receiver;
    private final String message;

    public PlayerMessageEvent(Player sender, Player receiver, String message) {
        super(sender);
        this.receiver = receiver;
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
