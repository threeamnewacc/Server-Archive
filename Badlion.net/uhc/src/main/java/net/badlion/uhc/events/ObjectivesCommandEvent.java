package net.badlion.uhc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ObjectivesCommandEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean sentMessages = false;

    public ObjectivesCommandEvent(Player player) {
        super(player);
    }

    public boolean isSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(boolean sentMessages) {
        this.sentMessages = sentMessages;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
