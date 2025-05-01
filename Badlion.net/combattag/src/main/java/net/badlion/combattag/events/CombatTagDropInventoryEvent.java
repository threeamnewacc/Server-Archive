package net.badlion.combattag.events;

import net.badlion.combattag.LoggerNPC;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CombatTagDropInventoryEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;

    private LoggerNPC loggerNPC;

	private ItemStack[] armor;
	private ItemStack[] inventory;

	private int droppedItemAge = 0;

    public CombatTagDropInventoryEvent(LoggerNPC loggerNPC, ItemStack[] armor, ItemStack[] inventory) {
        this.loggerNPC = loggerNPC;

	    this.armor = armor;
	    this.inventory = inventory;
    }

    public LoggerNPC getLoggerNPC() {
        return this.loggerNPC;
    }

	public ItemStack[] getArmor() {
		return this.armor;
	}

	public void setArmor(ItemStack[] armor) {
		this.armor = armor;
	}

	public ItemStack[] getInventory() {
		return this.inventory;
	}

	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}

	public int getDroppedItemAge() {
		return this.droppedItemAge;
	}

	public void setDroppedItemAge(int droppedItemAge) {
		this.droppedItemAge = droppedItemAge;
	}

	public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return CombatTagDropInventoryEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return CombatTagDropInventoryEvent.handlers;
    }

}
