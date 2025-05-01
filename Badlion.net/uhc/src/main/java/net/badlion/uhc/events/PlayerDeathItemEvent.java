package net.badlion.uhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathItemEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;
    private ItemStack itemStack;

    public PlayerDeathItemEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

}
