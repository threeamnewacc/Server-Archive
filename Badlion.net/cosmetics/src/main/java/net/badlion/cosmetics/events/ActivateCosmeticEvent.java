package net.badlion.cosmetics.events;

import net.badlion.cosmetics.CosmeticItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ActivateCosmeticEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;
    private CosmeticItem cosmeticItem;

    public ActivateCosmeticEvent(Player player, CosmeticItem cosmeticItem) {
        super(player);

        this.cosmeticItem = cosmeticItem;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CosmeticItem getCosmeticItem() {
        return cosmeticItem;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }


}
